package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.token.LoginType;
import com.mt.access.domain.model.token.TokenGrantClient;
import com.mt.access.domain.model.token.TokenGrantContext;
import com.mt.access.domain.model.token.TokenGrantType;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.access.domain.model.user.LoginUser;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserName;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.UserSession;
import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.access.domain.model.verification_code.VerificationCode;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenGrantService {
    public UserId newUser(TransactionContext txContext, TokenGrantContext context) {
        UserId userId = new UserId();
        LoginType type = context.getType();
        if (type.equals(LoginType.MOBILE_W_PWD) || type.equals(LoginType.MOBILE_W_CODE)) {
            UserMobile userMobile = context.getUserMobile();
            if (LoginType.MOBILE_W_PWD.equals(type)) {
                DomainRegistry.getNewUserService().create(
                    userMobile,
                    new UserPassword(context.getPassword()),
                    userId, txContext
                );
            } else {
                DomainRegistry.getNewUserService().create(
                    userMobile,
                    new VerificationCode(context.getCode()),
                    userId, txContext
                );
            }
        } else if (type.equals(LoginType.EMAIL_W_PWD) || type.equals(LoginType.EMAIL_W_CODE)) {
            UserEmail email = context.getEmail();
            if (LoginType.EMAIL_W_PWD.equals(type)) {
                DomainRegistry.getNewUserService().create(
                    email,
                    new UserPassword(context.getPassword()),
                    userId, txContext
                );
            } else {
                DomainRegistry.getNewUserService().create(
                    email,
                    new VerificationCode(context.getCode()),
                    userId, txContext
                );
            }
        } else {
            UserName username = new UserName(context.getUsername());
            DomainRegistry.getNewUserService().create(
                username,
                new UserPassword(context.getPassword()),
                userId,
                txContext
            );
        }
        return userId;
    }

    public boolean mfaRequired(TokenGrantContext context) {
        if (
            context.getGrantType().equals(TokenGrantType.CLIENT_CREDENTIALS) ||
                context.getGrantType().equals(TokenGrantType.REFRESH_TOKEN) ||
                context.getGrantType().equals(TokenGrantType.AUTHORIZATION_CODE)
        ) {
            return false;
        } else {
            //password
            if (context.getGrantType().equals(TokenGrantType.PASSWORD)) {
                if (Utility.isTrue(context.getNewUserRequired())) {
                    return false;
                }
                //existing user
                return !context.getType().equals(LoginType.MOBILE_W_CODE) &&
                    !context.getType().equals(LoginType.EMAIL_W_CODE);
            }
            return true;
        }
    }

    public void checkParam(TokenGrantContext context) {
        TokenGrantType grantType = context.getGrantType();
        boolean validParams = false;
        checkClientParam(context);
        if (grantType.equals(TokenGrantType.CLIENT_CREDENTIALS)) {
            validParams = true;
        } else if (grantType.equals(TokenGrantType.AUTHORIZATION_CODE)) {
            validParams =
                validAuthorizationCodeGrant(context.getScope(), context.getViewTenantId());
        } else if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
            validParams = validRefreshGrant(context.getRefreshToken());
        } else if (grantType.equals(TokenGrantType.PASSWORD)) {
            validParams = validPasswordGrant(context);
        }
        if (!validParams) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public String authorize(ProjectId projectId, ClientId clientId, String responseType,
                            String redirectUri) {
        Validator.notNull(responseType);
        Validator.notNull(redirectUri);
        if (!"code".equalsIgnoreCase(responseType)) {
            throw new DefinedRuntimeException("unsupported response types: " + responseType,
                "1006",
                HttpResponseCode.BAD_REQUEST);
        }
        Client client = DomainRegistry.getClientRepository().get(clientId);
        Set<ClientId> resources = DomainRegistry.getClientResourceRepository().query(client);
        Set<ClientId> extResources =
            DomainRegistry.getClientExternalResourceRepository().query(client);
        Set<RedirectUrl> urls = DomainRegistry.getClientRedirectUrlRepository().query(client);
        Set<GrantType> grantTypes = DomainRegistry.getClientGrantTypeRepository().query(client);
        TokenGrantClient tokenGrantClient =
            new TokenGrantClient(client, resources, extResources, urls, grantTypes);
        if (!tokenGrantClient.getRegisteredRedirectUri().contains(redirectUri)) {
            throw new DefinedRuntimeException(
                "unknown redirect url", "1008", HttpResponseCode.BAD_REQUEST);
        }
        return DomainRegistry.getTokenService()
            .authorize(
                redirectUri,
                clientId,
                Collections.singleton(projectId.getDomainId()),
                projectId,
                DomainRegistry.getCurrentUserService().getPermissionIds(),
                DomainRegistry.getCurrentUserService().getUserId()
            );
    }

    private boolean validPasswordGrant(TokenGrantContext context) {
        LoginType type = context.getType();
        if (Utility.isNull(type)) {
            return false;
        }
        boolean validParams = false;
        if (LoginType.MOBILE_W_CODE.equals(type)) {
            validParams = Utility.notNull(context.getUserMobile()) &&
                Utility.notNull(context.getCode());
        } else if (LoginType.EMAIL_W_CODE.equals(type)) {
            validParams = Utility.notNull(context.getEmail()) &&
                Utility.notNull(context.getCode());
        } else if (LoginType.USERNAME_W_PWD.equals(type)) {
            validParams = Utility.notNull(context.getUsername()) &&
                Utility.notNull(context.getPassword());
        } else if (LoginType.EMAIL_W_PWD.equals(type)) {
            validParams = Utility.notNull(context.getEmail()) &&
                Utility.notNull(context.getPassword());
        } else if (LoginType.MOBILE_W_PWD.equals(type)) {
            validParams = Utility.notNull(context.getUserMobile()) &&
                Utility.notNull(context.getPassword());
        }
        if (Utility.isFalse(validParams)) {
            return false;
        }
        LoginUser userInfo = null;
        context.setNewUserRequired(true);
        if (type.equals(LoginType.MOBILE_W_PWD) || type.equals(LoginType.MOBILE_W_CODE)) {
            UserMobile userMobile = context.getUserMobile();
            Optional<UserId> existUserId =
                DomainRegistry.getUserRepository().queryUserId(userMobile);
            if (existUserId.isPresent()) {
                context.setNewUserRequired(false);
                userInfo = DomainRegistry.getUserRepository().getLoginUser(existUserId.get());
                if (type.equals(LoginType.MOBILE_W_PWD)) {
                    if (!DomainRegistry.getEncryptionService()
                        .compare(context.getPassword(), userInfo.getPassword().getPassword())) {
                        throw new DefinedRuntimeException("wrong password", "1000",
                            HttpResponseCode.BAD_REQUEST);
                    }
                } else {
                    DomainRegistry.getTemporaryCodeService()
                        .verifyCode(context.getCode(),
                            VerificationCode.EXPIRE_AFTER_MILLI,
                            VerificationCode.OPERATION_TYPE,
                            new RegistrationMobile(context.getUserMobile().getCountryCode(),
                                context.getUserMobile().getMobileNumber()).getDomainId());
                }
            }
        } else if (type.equals(LoginType.EMAIL_W_PWD) || type.equals(LoginType.EMAIL_W_CODE)) {
            UserEmail email = context.getEmail();
            Optional<UserId> existUserId = DomainRegistry.getUserRepository().queryUserId(email);
            if (existUserId.isPresent()) {
                context.setNewUserRequired(false);
                userInfo = DomainRegistry.getUserRepository().getLoginUser(existUserId.get());
                if (type.equals(LoginType.EMAIL_W_PWD)) {
                    if (!DomainRegistry.getEncryptionService()
                        .compare(context.getPassword(), userInfo.getPassword().getPassword())) {
                        throw new DefinedRuntimeException("wrong password", "1000",
                            HttpResponseCode.BAD_REQUEST);
                    }
                } else {
                    DomainRegistry.getTemporaryCodeService()
                        .verifyCode(context.getCode(),
                            VerificationCode.EXPIRE_AFTER_MILLI,
                            VerificationCode.OPERATION_TYPE,
                            context.getEmail().getEmail());
                }
            }
        } else {
            UserName username = new UserName(context.getUsername());
            Optional<UserId> existUserId = DomainRegistry.getUserRepository().queryUserId(username);
            if (existUserId.isPresent()) {
                context.setNewUserRequired(false);
                userInfo = DomainRegistry.getUserRepository().getLoginUser(existUserId.get());
                if (!DomainRegistry.getEncryptionService()
                    .compare(context.getPassword(), userInfo.getPassword().getPassword())) {
                    throw new DefinedRuntimeException("wrong password", "1000",
                        HttpResponseCode.BAD_REQUEST);
                }
            }
        }
        if (Utility.notNull(userInfo) && Utility.isTrue(userInfo.getLocked())) {
            validParams = false;
        }
        if (context.getNewUserRequired() && Utility.isNull(context.getChangeId())) {
            validParams = false;
        }
        context.setLoginUser(userInfo);
        return validParams;
    }

    private boolean validAuthorizationCodeGrant(ProjectId scope, ProjectId viewTenantId) {
        return !(Utility.notNull(viewTenantId) && Utility.notNull(scope));
    }

    private boolean validRefreshGrant(String refreshToken) {
        Integer expInSec = JwtUtility.getField("exp", refreshToken);
        if (Instant.now().isAfter(Instant.ofEpochSecond(expInSec))) {
            throw new DefinedRuntimeException("refresh token expired", "1090",
                HttpResponseCode.UNAUTHORIZED);
        }
        return Utility.notBlank(refreshToken);
    }


    private void checkClientParam(TokenGrantContext context) {
        ClientId clientId = context.getClientId();
        String clientSecret = context.getClientSecret();
        TokenGrantType grantType = context.getGrantType();
        Client client = DomainRegistry.getClientRepository().query(clientId);
        if (client == null) {
            throw new DefinedRuntimeException("client not found", "1091",
                HttpResponseCode.UNAUTHORIZED);
        }
        if (!Utility.equals(clientSecret, client.getSecret())) {
            throw new DefinedRuntimeException("wrong client secret", "1070",
                HttpResponseCode.UNAUTHORIZED);
        }
        Set<GrantType> grantTypes = DomainRegistry.getClientGrantTypeRepository().query(client);
        if (grantType.equals(TokenGrantType.REFRESH_TOKEN)) {
            if (!grantTypes.contains(GrantType.REFRESH_TOKEN)) {
                throw new DefinedRuntimeException("invalid params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        if (grantType.equals(TokenGrantType.PASSWORD)) {
            if (!grantTypes.contains(GrantType.PASSWORD)) {
                throw new DefinedRuntimeException("invalid params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        if (grantType.equals(TokenGrantType.CLIENT_CREDENTIALS)) {
            if (!grantTypes.contains(GrantType.CLIENT_CREDENTIALS)) {
                throw new DefinedRuntimeException("invalid params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        if (grantType.equals(TokenGrantType.AUTHORIZATION_CODE)) {
            if (!grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
                throw new DefinedRuntimeException("invalid params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        Set<ClientId> resources = DomainRegistry.getClientResourceRepository().query(client);
        Set<ClientId> extResources =
            DomainRegistry.getClientExternalResourceRepository().query(client);
        Set<RedirectUrl> urls = DomainRegistry.getClientRedirectUrlRepository().query(client);
        context.setClient(new TokenGrantClient(client, resources, extResources, urls, grantTypes));
    }


    public void userLoginCheck(TokenGrantContext context) {
        String mfaMethod = context.getMfaMethod();
        String mfaCode = context.getMfaCode();
        String ipAddress = context.getIpAddress();
        UserId userId = context.getLoginUser().getUserId();
        log.debug("user id {}", userId.getDomainId());
        User user = DomainRegistry.getUserRepository().get(userId);
        if (user.hasNoMfaOptions()) {
            log.debug("mfa not found, record current login information");
            context.setRecordLoginRequired(true);
            context.setLoginResult(LoginResult.allow());
            return;
        }
        boolean mfaRequired =
            DomainRegistry.getMfaService().isMfaRequired(userId, new UserSession(ipAddress));
        if (!mfaRequired) {
            log.debug("mfa not required, record current login information");
            context.setRecordLoginRequired(true);
            context.setLoginResult(LoginResult.allow());
        } else {
            if (mfaCode != null) {
                log.debug("mfa code present");
                if (DomainRegistry.getMfaService().validateMfa(userId, mfaCode)) {
                    log.debug("mfa check passed, record current login information");
                    context.setLoginResult(LoginResult.allow());
                } else {
                    log.debug("mfa check failed");
                    context.setLoginResult(LoginResult.mfaMissMatch());
                }
            } else {
                MfaDeliverMethod deliverMethod = MfaDeliverMethod.parse(mfaMethod);
                if (Utility.notNull(deliverMethod)) {
                    log.debug("mfa required and user selected deliver method");
                    context.setTriggerMfaRequired(true);
                    context.setDeliveryMethod(deliverMethod);
                    context.setMfaUser(user);
                    context.setLoginResult(LoginResult
                        .mfaMissingAfterSelect(deliverMethod, user));
                } else {
                    log.debug("mfa required and need input by user");
                    if (user.hasMultipleMfaOptions()) {
                        log.debug("asking user to pick mfa deliver method");
                        context.setLoginResult(LoginResult
                            .askUserSelect(user));
                    } else {
                        context.setTriggerDefaultMfaRequired(true);
                        context.setMfaUser(user);
                        context.setLoginResult(LoginResult
                            .mfaMissing(user));
                    }
                }
            }
        }
    }


}
