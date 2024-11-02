package com.mt.access.application.user;

import static com.mt.access.domain.model.audit.AuditActionName.MGMT_LOCK_USER;
import static com.mt.access.domain.model.audit.AuditActionName.USER_FORGET_PWD;
import static com.mt.access.domain.model.audit.AuditActionName.USER_RESET_PWD;
import static com.mt.access.domain.model.audit.AuditActionName.USER_UPDATE_PWD;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.UpdateUserCommand;
import com.mt.access.application.user.command.UserAddEmailCommand;
import com.mt.access.application.user.command.UserAddMobileCommand;
import com.mt.access.application.user.command.UserAddUserNameCommand;
import com.mt.access.application.user.command.UserForgetPasswordCommand;
import com.mt.access.application.user.command.UserResetPasswordCommand;
import com.mt.access.application.user.command.UserUpdateLanguageCommand;
import com.mt.access.application.user.command.UserUpdatePasswordCommand;
import com.mt.access.application.user.representation.UserMgmtRepresentation;
import com.mt.access.application.user.representation.UserProfileRepresentation;
import com.mt.access.application.user.representation.UserTokenRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.Language;
import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.access.domain.model.user.LoginUser;
import com.mt.access.domain.model.user.PwdResetCode;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserAvatar;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserLoginRequest;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserName;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.UserQuery;
import com.mt.access.domain.model.user.UserSession;
import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.access.domain.model.verification_code.VerificationCode;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UserApplicationService {

    private static final String USER = "User";

    public UserProfileRepresentation myProfile() {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().query(userId);
        return new UserProfileRepresentation(user, loginInfo.orElse(null));
    }


    public SumPagedRep<User> query(String queryParam, String pageParam, String config) {
        return DomainRegistry.getUserRepository()
            .query(new UserQuery(queryParam, pageParam, config));
    }

    public Set<User> query(Set<UserId> userIdSet) {
        return QueryUtility.getAllByQuery(e -> DomainRegistry.getUserRepository()
            .query(e), new UserQuery(userIdSet));
    }

    public Optional<User> query(String userId) {
        return DomainRegistry.getUserRepository().query(new UserQuery(new UserId(userId)))
            .findFirst();
    }

    public UserMgmtRepresentation mgmtQuery(String userId) {
        User user = DomainRegistry.getUserRepository().get(new UserId(userId));
        Set<LoginHistory> allForUser =
            DomainRegistry.getLoginHistoryRepository().getLast100Login(user.getUserId());
        return new UserMgmtRepresentation(user, allForUser);
    }

    public UserTokenRepresentation getUserByEmailOrId(String emailOrId) {
        log.debug("loading user by username started");
        LoginUser user;
        if (Checker.isEmail(emailOrId)) {
            //for login
            user =
                DomainRegistry.getUserRepository().getLoginUser(new UserEmail(emailOrId));
        } else {
            //for refresh token
            user = DomainRegistry.getUserRepository().getLoginUser(new UserId(emailOrId));
        }
        log.debug("loading user by username end");
        return new UserTokenRepresentation(user);
    }

    public UserTokenRepresentation getUserBy(UserId userId) {
        LoginUser user = DomainRegistry.getUserRepository().getLoginUser(userId);
        return new UserTokenRepresentation(user);
    }

    @AuditLog(actionName = MGMT_LOCK_USER)
    public void mgmtLock(String id, UpdateUserCommand command, String changeId) {
        DomainRegistry.getAuditService()
            .logUserAction(log, "lock user",
                "with user id :" + id);
        UserId userId = new UserId(id);
        User user = DomainRegistry.getUserRepository().get(userId);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                User user1 = user.lockUser(
                    command.getLocked(), context
                );
                DomainRegistry.getUserRepository().update(user, user1);
                return null;
            }, USER);
    }

    @AuditLog(actionName = USER_UPDATE_PWD)
    public void updatePassword(UserUpdatePasswordCommand command, String changeId) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                User user = DomainRegistry.getUserRepository().get(userId);
                DomainRegistry.getUserService()
                    .updatePassword(user, Checker.notNull(command.getCurrentPwd()) ?
                            new CurrentPassword(command.getCurrentPwd()) : null,
                        new UserPassword(command.getPassword()), context);
                return null;
            }, USER);
    }

    public void forgetPassword(UserForgetPasswordCommand command, String changeId) {
        if (Checker.notNull(command.getEmail())) {
            UserEmail email = new UserEmail(command.getEmail());
            forgetPasswordEmail(command.getClientId(), email, changeId);
        } else {
            UserMobile mobile =
                new UserMobile(command.getCountryCode(), command.getMobileNumber());
            forgetPasswordMobile(command.getClientId(), mobile, changeId);
        }
    }

    private void forgetPasswordMobile(ClientId clientId, UserMobile mobile, String changeId) {
        DomainRegistry.getAuditService()
            .logExternalUserAction(log, mobile.getValue(), USER_FORGET_PWD);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getPwdResetService().forgetPwd(clientId, mobile, context);
                return null;
            }, USER);
    }

    private void forgetPasswordEmail(ClientId clientId, UserEmail email, String changeId) {
        DomainRegistry.getAuditService()
            .logExternalUserAction(log, email.getEmail(), USER_FORGET_PWD);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getPwdResetService().forgetPwd(clientId, email, context);
                return null;
            }, USER);
    }

    public void resetPassword(UserResetPasswordCommand command, String changeId) {
        if (Checker.notNull(command.getEmail())) {
            UserEmail email = new UserEmail(command.getEmail());
            resetPasswordEmail(email, new UserPassword(command.getNewPassword()),
                new PwdResetCode(command.getToken()), changeId);
        } else {
            UserMobile userMobile =
                new UserMobile(command.getCountryCode(), command.getMobileNumber());
            resetPasswordMobile(userMobile, new UserPassword(command.getNewPassword()),
                new PwdResetCode(command.getToken()), changeId);
        }
    }

    private void resetPasswordEmail(UserEmail email, UserPassword newPwd, PwdResetCode code,
                                    String changeId) {
        DomainRegistry.getAuditService()
            .logExternalUserAction(log, email.getEmail(), USER_RESET_PWD);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getPwdResetService()
                    .resetPassword(email, newPwd, code, context);
                return null;
            }, USER);
    }

    private void resetPasswordMobile(UserMobile mobile, UserPassword newPwd,
                                     PwdResetCode code, String changeId) {
        DomainRegistry.getAuditService()
            .logExternalUserAction(log, mobile.getValue(), USER_RESET_PWD);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getPwdResetService()
                    .resetPassword(mobile, newPwd, code, context);
                return null;
            }, USER);
    }


    public Optional<Image> queryProfileAvatar() {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        if (user.getUserAvatar() != null) {
            String value = user.getUserAvatar().getValue();
            return ApplicationServiceRegistry.getImageApplicationService()
                .queryById(new ImageId(value));
        } else {
            return Optional.empty();
        }
    }

    public ImageId createProfileAvatar(MultipartFile file, String changeId) {
        ImageId imageId =
            ApplicationServiceRegistry.getImageApplicationService().create(changeId, file);
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        User updated = user.updateUserAvatar(new UserAvatar(imageId));
        DomainRegistry.getUserRepository().update(user, updated);
        return imageId;
    }

    public LoginResult userLoginCheck(String ipAddress, String agentInfo,
                                      String rawUserId, @Nullable String mfaCode,
                                      @Nullable String mfaMethod,
                                      ProjectId loginProjectId) {
        UserId userId = new UserId(rawUserId);
        log.debug("user id {}", userId.getDomainId());
        User user1 = DomainRegistry.getUserRepository().get(userId);
        if (user1.hasNoMfaOptions()) {
            log.debug("mfa not found, record current login information");
            recordLoginInfo(ipAddress, agentInfo, userId, loginProjectId);
            return LoginResult.allow();
        }
        boolean mfaRequired =
            DomainRegistry.getMfaService().isMfaRequired(userId, new UserSession(ipAddress));
        if (!mfaRequired) {
            log.debug("mfa not required, record current login information");
            recordLoginInfo(ipAddress, agentInfo, userId, loginProjectId);
            return LoginResult.allow();
        } else {
            if (mfaCode != null) {
                log.debug("mfa code present");
                if (DomainRegistry.getMfaService().validateMfa(userId, mfaCode)) {
                    log.debug("mfa check passed, record current login information");
                    recordLoginInfo(ipAddress, agentInfo, userId, loginProjectId);
                    return LoginResult.allow();
                } else {
                    log.debug("mfa check failed");
                    return LoginResult.mfaMissMatch();
                }
            } else {
                MfaDeliverMethod deliverMethod = MfaDeliverMethod.parse(mfaMethod);
                if (Checker.notNull(deliverMethod)) {
                    log.debug("mfa required and user selected deliver method");
                    CommonDomainRegistry.getTransactionService()
                        .transactionalEvent(
                            (context) -> DomainRegistry.getMfaService()
                                .triggerSelectedMfa(user1, context, deliverMethod));
                    return LoginResult
                        .mfaMissingAfterSelect(deliverMethod, user1);
                } else {
                    log.debug("mfa required and need input by user");
                    if (user1.hasMultipleMfaOptions()) {
                        log.debug("asking user to pick mfa deliver method");
                        return LoginResult
                            .askUserSelect(user1);
                    } else {
                        CommonDomainRegistry.getTransactionService()
                            .transactionalEvent(
                                (context) -> DomainRegistry.getMfaService()
                                    .triggerDefaultMfa(user1, context));
                        return LoginResult
                            .mfaMissing(user1);
                    }
                }
            }
        }
    }

    private void recordLoginInfo(String ipAddress, String agentInfo, UserId userId,
                                 ProjectId loginProjectId) {
        UserLoginRequest userLoginRequest =
            new UserLoginRequest(ipAddress, userId, agentInfo);
        CommonDomainRegistry.getTransactionService()
            .transactionalEvent(
                (context) -> DomainRegistry.getUserService()
                    .updateLastLogin(userLoginRequest, loginProjectId));
    }

    public Optional<UserId> checkExistingUser(UserMobile userMobile) {
        return DomainRegistry.getUserRepository().queryUserId(userMobile);
    }

    public Optional<UserId> checkExistingUser(UserEmail email) {
        return DomainRegistry.getUserRepository().queryUserId(email);
    }

    public Optional<UserId> checkExistingUser(UserName username) {
        return DomainRegistry.getUserRepository().queryUserId(username);
    }

    public String createUserUsing(UserMobile userMobile,
                                  UserPassword userPassword,
                                  String changeId
    ) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        userMobile,
                        userPassword,
                        userId, context
                    );
                    return userId1.getDomainId();
                }, USER
            );
    }

    public String createUserUsingCodeAnd(UserMobile userMobile,
                                         VerificationCode code,
                                         String changeId) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        userMobile,
                        code,
                        userId, context
                    );
                    return userId1.getDomainId();
                }, USER
            );
    }

    public String createUserUsing(UserEmail email, UserPassword userPassword, String changeId) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        email,
                        userPassword,
                        userId, context
                    );
                    return userId1.getDomainId();
                }, USER
            );
    }

    public String createUserUsingCodeAnd(UserEmail email, VerificationCode code,
                                         String changeId) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        email,
                        code,
                        userId, context
                    );
                    return userId1.getDomainId();
                }, USER
            );
    }

    public String createUserUsing(UserName username, UserPassword password, String changeId) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        username,
                        password,
                        userId, context
                    );
                    return userId1.getDomainId();
                }, USER
            );
    }

    public void addUsername(UserAddUserNameCommand command, String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        UserName newUserName = new UserName(command.getUsername());
        User user = DomainRegistry.getUserRepository().get(userId);
        Validator.isNull(user.getUserName());
        Optional<UserId> userId1 = DomainRegistry.getUserRepository().queryUserId(newUserName);
        if (userId1.isPresent()) {
            throw new DefinedRuntimeException("mobile, email, or username already used",
                "1093",
                HttpResponseCode.BAD_REQUEST);
        }
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    User newUser = user.addUserName(newUserName);
                    DomainRegistry.getUserRepository().update(user, newUser);
                    return null;
                }, USER
            );
    }

    public void deleteUsername(String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        Validator.notNull(user.getUserName());
        user.checkUserNameRemoval();
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    User newUser = user.removeUserName();
                    DomainRegistry.getUserRepository().update(user, newUser);
                    return null;
                }, USER
            );
    }

    public void addMobile(UserAddMobileCommand command, String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        UserMobile newMobile = new UserMobile(command.getCountryCode(), command.getMobileNumber());
        User user = DomainRegistry.getUserRepository().get(userId);
        Validator.isNull(user.getMobile());
        Optional<UserId> userId1 = DomainRegistry.getUserRepository().queryUserId(newMobile);
        if (userId1.isPresent()) {
            throw new DefinedRuntimeException("mobile, email, or username already used",
                "1093",
                HttpResponseCode.BAD_REQUEST);
        }
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    User newUser = user.addMobile(newMobile);
                    DomainRegistry.getUserRepository().update(user, newUser);
                    return null;
                }, USER
            );
    }

    public void deleteMobile(String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        Validator.notNull(user.getMobile());
        user.checkMobileRemoval();
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    User newUser = user.removeMobile();
                    DomainRegistry.getUserRepository().update(user, newUser);
                    return null;
                }, USER
            );
    }

    public void addEmail(UserAddEmailCommand command, String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        UserEmail userEmail = new UserEmail(command.getEmail());
        User user = DomainRegistry.getUserRepository().get(userId);
        Validator.isNull(user.getEmail());
        Optional<UserId> userId1 = DomainRegistry.getUserRepository().queryUserId(userEmail);
        if (userId1.isPresent()) {
            throw new DefinedRuntimeException("mobile, email, or username already used",
                "1093",
                HttpResponseCode.BAD_REQUEST);
        }
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    User newUser = user.addEmail(userEmail);
                    DomainRegistry.getUserRepository().update(user, newUser);
                    return null;
                }, USER
            );
    }

    public void deleteEmail(String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        Validator.notNull(user.getEmail());
        user.checkEmailRemoval();
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    User newUser = user.removeEmail();
                    DomainRegistry.getUserRepository().update(user, newUser);
                    return null;
                }, USER
            );
    }

    public void updateLanguage(UserUpdateLanguageCommand command) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Language language = Language.parse(command.getLanguage());
        Validator.notNull(language);
        User user = DomainRegistry.getUserRepository().get(userId);
        CommonDomainRegistry.getTransactionService()
            .transactionalEvent(
                (context) -> {
                    User newUser = user.updateLanguage(language);
                    DomainRegistry.getUserRepository().update(user, newUser);
                }
            );
    }
}
