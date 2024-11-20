package com.mt.access.application.token;

import static com.mt.access.application.user.UserApplicationService.USER;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.token.TokenGrantContext;
import com.mt.access.domain.model.token.TokenGrantType;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserLoginRequest;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenApplicationService {
    public String authorize(String projectId, String clientId, String responseType,
                            String redirectUri) {
        Validator.notNull(clientId);
        return DomainRegistry.getTokenGrantService()
            .authorize(new ProjectId(projectId), new ClientId(clientId), responseType, redirectUri);
    }

    public TokenGrantContext grantToken(String clientId, String clientSecret, String agentInfo,
                                        String ipAddress, String changeId, String grantType,
                                        String scope, String refreshToken,
                                        String viewTenantId, String type, String mobileNumber,
                                        String countryCode, String code, String email,
                                        String username,
                                        String password, String mfaMethod, String mfaCode,
                                        String redirectUri) {

        TokenGrantContext context = new TokenGrantContext(
            new ClientId(clientId), clientSecret, agentInfo, ipAddress, changeId,
            TokenGrantType.parse(grantType),
            scope, refreshToken, viewTenantId, type, mobileNumber, countryCode, code, email,
            username, password, mfaMethod, mfaCode, redirectUri
        );
        DomainRegistry.getTokenGrantService().checkParam(context);
        if (Checker.isTrue(context.getNewUserRequired())) {
            String userId = CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId,
                    (txContext) -> DomainRegistry.getTokenGrantService().newUser(txContext, context)
                        .getDomainId(), USER
                );
            context.setLoginUser(
                DomainRegistry.getUserRepository().getLoginUser(new UserId(userId)));
        } else {
            if (DomainRegistry.getTokenGrantService().mfaRequired(context)) {
                DomainRegistry.getTokenGrantService().userLoginCheck(context);
                if (Checker.isTrue(context.getTriggerMfaRequired())) {
                    CommonDomainRegistry.getTransactionService()
                        .transactionalEvent(
                            (txContext) -> DomainRegistry.getMfaService()
                                .triggerSelectedMfa(context.getClientId(), context.getMfaUser(),
                                    txContext, context.getDeliveryMethod()));
                }
                if (Checker.isTrue(context.getTriggerDefaultMfaRequired())) {
                    CommonDomainRegistry.getTransactionService()
                        .transactionalEvent(
                            (txContext) -> DomainRegistry.getMfaService()
                                .triggerDefaultMfa(context.getClientId(), context.getMfaUser(),
                                    txContext));
                }
                if (Checker.isTrue(context.getRecordLoginRequired())) {
                    UserLoginRequest userLoginRequest =
                        new UserLoginRequest(ipAddress, context.getLoginUser().getUserId(),
                            agentInfo);
                    CommonDomainRegistry.getTransactionService()
                        .transactionalEvent(
                            (txContext) -> DomainRegistry.getUserService()
                                .updateLastLogin(userLoginRequest, context.getParsedScope()));
                }
                if (Checker.isFalse(context.getLoginResult().getAllowed())) {
                    return context;
                }
            }
        }
        CommonDomainRegistry.getTransactionService()
            .transactionalEvent((txContext) -> DomainRegistry.getTokenService().grant(context));
        return context;
    }
}
