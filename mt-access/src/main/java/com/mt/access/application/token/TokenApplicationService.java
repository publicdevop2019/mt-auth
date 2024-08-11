package com.mt.access.application.token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.representation.ClientOAuth2Representation;
import com.mt.access.application.token.representation.JwtTokenRepresentation;
import com.mt.access.application.user.representation.UserTokenRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.Code;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.token.JwtToken;
import com.mt.access.domain.model.token.LoginType;
import com.mt.access.domain.model.token.TokenGrantContext;
import com.mt.access.domain.model.token.TokenGrantType;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserName;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenApplicationService {

    private static final String MFA_REQUIRED = "mfa required";

    public ResponseEntity<?> grantToken(String clientId, String clientSecret,
                                        Map<String, String> parameters,
                                        String agentInfo, String clientIpAddress,
                                        String changeId) {
        TokenGrantType grantType = TokenGrantType.parse(parameters.get("grant_type"));
        boolean validParams = false;
        TokenGrantContext tokenGrantContext = null;
        UserTokenRepresentation userInfo = null;
        ClientOAuth2Representation client = null;
        String scope = parameters.get("scope");
        if (Checker.notNull(grantType)) {
            //check client first
            client =
                ApplicationServiceRegistry.getClientApplicationService()
                    .getClientBy(clientId);
            if (client == null) {
                throw new DefinedRuntimeException("client not found", "1091",
                    HttpResponseCode.UNAUTHORIZED);
            }
            if (!Checker.equals(clientSecret, client.getClientSecret())) {
                throw new DefinedRuntimeException("wrong client password", "1070",
                    HttpResponseCode.UNAUTHORIZED);
            }
            if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
                if (!client.getAuthorizedGrantTypes().contains("refresh_token")) {
                    throw new DefinedRuntimeException("invalid params", "1089",
                        HttpResponseCode.BAD_REQUEST);
                }
                Integer expInSec = JwtUtility.getField("exp", parameters.get("refresh_token"));
                if (Instant.now().isAfter(Instant.ofEpochSecond(expInSec))) {
                    throw new DefinedRuntimeException("refresh token expired", "1090",
                        HttpResponseCode.UNAUTHORIZED);
                }
            }

            if (grantType.equals(TokenGrantType.AUTHORIZATION_CODE)) {
                validParams = validAuthorizationCodeGrant(scope,
                    parameters.get("view_tenant_id"));
            } else {
                if (grantType.equals(TokenGrantType.CLIENT_CREDENTIALS)) {
                    validParams = true;
                } else if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
                    validParams = validRefreshGrant(parameters.get("refresh_token"));
                } else if (grantType.equals(TokenGrantType.PASSWORD)) {
                    validParams = true;
                    tokenGrantContext = checkPasswordGrant(
                        parameters.get("type"),
                        parameters.get("mobile_number"),
                        parameters.get("country_code"),
                        parameters.get("code"),
                        parameters.get("email"),
                        parameters.get("username"),
                        parameters.get("password"),
                        changeId
                    );
                    userInfo = tokenGrantContext.getUserInfo();
                }
            }

        }
        if (!validParams) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }

        //MFA check
        if (grantType.equals(TokenGrantType.PASSWORD) && tokenGrantContext.isMFARequired()) {
            log.debug("checking user mfa");
            AtomicReference<LoginResult> loginResult = new AtomicReference<>();
            UserTokenRepresentation finalUserInfo1 = userInfo;
            CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                loginResult.set(ApplicationServiceRegistry.getUserApplicationService()
                    .userLoginCheck(clientIpAddress, agentInfo, finalUserInfo1.getId(),
                        parameters.get("mfa_code"),
                        parameters.get("mfa_id"),
                        hasScope(scope) ? new ProjectId(scope) :
                            new ProjectId(AppConstant.MT_AUTH_PROJECT_ID)
                    )
                );
            });
            if (Checker.isFalse(loginResult.get().getAllowed())) {
                if (Checker.isTrue(loginResult.get().getInvalidMfa())) {
                    log.debug("invalid mfa");
                    return ResponseEntity.badRequest().build();
                } else {
                    log.debug("asking mfa");
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put("message", MFA_REQUIRED);
                    stringStringHashMap.put("mfaId", loginResult.get().getMfaId().getValue());
                    return ResponseEntity.ok().body(stringStringHashMap);
                }
            }
        }

        AtomicReference<JwtToken> token = new AtomicReference<>();
        log.debug("end of all checks");
        UserTokenRepresentation finalUserInfo = userInfo;
        ClientOAuth2Representation finalClient = client;
        CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
            JwtToken grant =
                DomainRegistry.getTokenService().grant(parameters, finalClient, finalUserInfo);
            token.set(grant);
        });
        return ResponseEntity.ok(new JwtTokenRepresentation(token.get()));
    }

    private TokenGrantContext checkPasswordGrant(String rawType, String mobileNumber,
                                                 String countryCode,
                                                 String code, String rawEmail,
                                                 String rawUsername,
                                                 String enteredPwd,
                                                 String changeId) {
        TokenGrantContext tokenGrantContext = new TokenGrantContext();
        boolean validParams = false;
        Optional<UserId> existUserId;
        LoginType type = LoginType.parse(rawType);
        if (Checker.notNull(type) && Checker.notNull(changeId)) {
            if (LoginType.MOBILE_W_CODE.equals(type)) {
                validParams = Checker.notNull(mobileNumber) &&
                    Checker.notNull(countryCode) &&
                    Checker.notNull(code);
            } else if (LoginType.EMAIL_W_CODE.equals(type)) {
                validParams = Checker.notNull(rawEmail) &&
                    Checker.notNull(code);
            } else if (LoginType.USERNAME_W_PWD.equals(type)) {
                validParams = Checker.notNull(rawUsername) &&
                    Checker.notNull(enteredPwd);
            } else if (LoginType.EMAIL_W_PWD.equals(type)) {
                validParams = Checker.notNull(rawEmail) &&
                    Checker.notNull(enteredPwd);
            } else if (LoginType.MOBILE_W_PWD.equals(type)) {
                validParams = Checker.notNull(mobileNumber) &&
                    Checker.notNull(countryCode) &&
                    Checker.notNull(enteredPwd);
            }
        }
        if (!validParams) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        UserTokenRepresentation userInfo;

        if (type.equals(LoginType.MOBILE_W_PWD) || type.equals(LoginType.MOBILE_W_CODE)) {
            UserMobile userMobile = new UserMobile(countryCode, mobileNumber);
            existUserId = ApplicationServiceRegistry.getUserApplicationService()
                .checkExistingUser(userMobile);
            if (existUserId.isEmpty()) {
                tokenGrantContext.setMFARequired(false);
                String createdUserId;
                if (LoginType.MOBILE_W_PWD.equals(type)) {
                    createdUserId = ApplicationServiceRegistry.getUserApplicationService()
                        .createUserUsing(userMobile, new UserPassword(enteredPwd), changeId);
                } else {
                    createdUserId = ApplicationServiceRegistry.getUserApplicationService()
                        .createUserUsingCodeAnd(userMobile, new Code(code), changeId);
                }
                userInfo = ApplicationServiceRegistry.getUserApplicationService()
                    .getUserBy(new UserId(createdUserId));
            } else {
                userInfo = ApplicationServiceRegistry.getUserApplicationService()
                    .getUserBy(existUserId.get());
                if (type.equals(LoginType.MOBILE_W_PWD)) {
                    if (!DomainRegistry.getEncryptionService()
                        .compare(enteredPwd, userInfo.getUserPassword().getPassword())) {
                        throw new DefinedRuntimeException("wrong password", "1000",
                            HttpResponseCode.BAD_REQUEST);
                    }
                } else {
                    tokenGrantContext.setMFARequired(false);
                    ApplicationServiceRegistry.getVerificationCodeApplicationService()
                        .checkCode(countryCode, mobileNumber, code);
                }
            }
        } else if (type.equals(LoginType.EMAIL_W_PWD) || type.equals(LoginType.EMAIL_W_CODE)) {
            UserEmail email = new UserEmail(rawEmail);
            existUserId = ApplicationServiceRegistry.getUserApplicationService()
                .checkExistingUser(email);
            if (existUserId.isEmpty()) {
                tokenGrantContext.setMFARequired(false);
                String createdUserId;
                if (LoginType.EMAIL_W_PWD.equals(type)) {
                    createdUserId = ApplicationServiceRegistry.getUserApplicationService()
                        .createUserUsing(email, new UserPassword(enteredPwd), changeId);
                } else {
                    createdUserId = ApplicationServiceRegistry.getUserApplicationService()
                        .createUserUsingCodeAnd(email, new Code(code), changeId);
                }
                userInfo = ApplicationServiceRegistry.getUserApplicationService()
                    .getUserBy(new UserId(createdUserId));
            } else {
                userInfo = ApplicationServiceRegistry.getUserApplicationService()
                    .getUserBy(existUserId.get());
                if (type.equals(LoginType.EMAIL_W_PWD)) {
                    if (!DomainRegistry.getEncryptionService()
                        .compare(enteredPwd,
                            userInfo.getUserPassword().getPassword())) {
                        throw new DefinedRuntimeException("wrong password", "1000",
                            HttpResponseCode.BAD_REQUEST);
                    }
                } else {
                    tokenGrantContext.setMFARequired(false);
                    ApplicationServiceRegistry.getVerificationCodeApplicationService()
                        .checkCode(rawEmail, code);
                }
            }
        } else {
            UserName username = new UserName(rawUsername);
            existUserId = ApplicationServiceRegistry.getUserApplicationService()
                .checkExistingUser(username);
            if (existUserId.isEmpty()) {
                tokenGrantContext.setMFARequired(false);
                String createdUserId = ApplicationServiceRegistry.getUserApplicationService()
                    .createUserUsing(username, new UserPassword(enteredPwd), changeId);
                userInfo = ApplicationServiceRegistry.getUserApplicationService()
                    .getUserBy(new UserId(createdUserId));
            } else {
                userInfo = ApplicationServiceRegistry.getUserApplicationService()
                    .getUserBy(existUserId.get());
                if (!DomainRegistry.getEncryptionService()
                    .compare(enteredPwd,
                        userInfo.getUserPassword().getPassword())) {
                    throw new DefinedRuntimeException("wrong password", "1000",
                        HttpResponseCode.BAD_REQUEST);
                }
            }
        }
        if (!userInfo.isAccountNonLocked()) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        tokenGrantContext.setUserInfo(userInfo);
        return tokenGrantContext;
    }

    private boolean validAuthorizationCodeGrant(String scope, String viewTenantId) {
        return !(Checker.notNull(viewTenantId) && hasScope(scope));
    }

    private boolean hasScope(String scope) {
        return Checker.notNull(scope) && !"not_used".equalsIgnoreCase(scope);
    }

    private boolean validRefreshGrant(String refreshToken) {
        return Checker.notBlank(refreshToken);
    }

    /**
     * consume authorize request.
     *
     * @param parameters request params
     * @return authorization response params
     */
    public String authorize(Map<String, String> parameters) {
        String clientId = parameters.get("client_id");
        String responseType = parameters.get("response_type");
        String redirectUri = parameters.get("redirect_uri");
        Validator.notNull(clientId);
        Validator.notNull(responseType);
        Validator.notNull(redirectUri);
        if (!"code".equalsIgnoreCase(responseType)) {
            throw new DefinedRuntimeException("unsupported response types: " + responseType,
                "1006",
                HttpResponseCode.BAD_REQUEST);
        }

        ClientOAuth2Representation client =
            ApplicationServiceRegistry.getClientApplicationService()
                .getClientBy(clientId);

        if (client == null) {
            throw new DefinedRuntimeException(
                "unable to find authorize client", "1005",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!client.getRegisteredRedirectUri().contains(redirectUri)) {
            throw new DefinedRuntimeException(
                "unknown redirect url", "1008", HttpResponseCode.BAD_REQUEST);
        }
        return DomainRegistry.getTokenService()
            .authorize(
                redirectUri, clientId, Collections.singleton(parameters.get("project_id")),
                new ProjectId(parameters.get("project_id")),
                DomainRegistry.getCurrentUserService().getPermissionIds(),
                DomainRegistry.getCurrentUserService().getUserId()
            );
    }
}
