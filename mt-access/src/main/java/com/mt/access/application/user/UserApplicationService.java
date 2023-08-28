package com.mt.access.application.user;

import static com.mt.access.domain.model.audit.AuditActionName.MGMT_DELETE_USER;
import static com.mt.access.domain.model.audit.AuditActionName.MGMT_LOCK_USER;
import static com.mt.access.domain.model.audit.AuditActionName.MGMT_PATCH_BATCH_USER;
import static com.mt.access.domain.model.audit.AuditActionName.MGMT_PATCH_USER;
import static com.mt.access.domain.model.audit.AuditActionName.USER_FORGET_PWD;
import static com.mt.access.domain.model.audit.AuditActionName.USER_RESET_PWD;
import static com.mt.access.domain.model.audit.AuditActionName.USER_UPDATE_PROFILE;
import static com.mt.access.domain.model.audit.AuditActionName.USER_UPDATE_PWD;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.UpdateUserCommand;
import com.mt.access.application.user.command.UserCreateCommand;
import com.mt.access.application.user.command.UserForgetPasswordCommand;
import com.mt.access.application.user.command.UserPatchingCommand;
import com.mt.access.application.user.command.UserResetPasswordCommand;
import com.mt.access.application.user.command.UserUpdatePasswordCommand;
import com.mt.access.application.user.command.UserUpdateProfileCommand;
import com.mt.access.application.user.representation.UserMgmtRepresentation;
import com.mt.access.application.user.representation.UserProfileRepresentation;
import com.mt.access.application.user.representation.UserSpringRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.access.domain.model.user.LoginUser;
import com.mt.access.domain.model.user.MfaId;
import com.mt.access.domain.model.user.PasswordResetCode;
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
import com.mt.access.domain.model.user.event.UserDeleted;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UserApplicationService implements UserDetailsService {

    private static final String USER = "User";
    private static final String DEFAULT_USERID = "0U8AZTODP4H0";


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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("loading user by username started");
        LoginUser user;
        if (Checker.isEmail(username)) {
            //for login
            user =
                DomainRegistry.getUserRepository().getLoginUser(new UserEmail(username));
        } else {
            //for refresh token
            user = DomainRegistry.getUserRepository().getLoginUser(new UserId(username));
        }
        log.debug("loading user by username end");
        return new UserSpringRepresentation(user);
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
                user.lockUser(
                    command.getLocked(), context
                );
                return null;
            }, USER);
        DomainRegistry.getUserRepository().add(user);
    }

    public String create(UserCreateCommand command, String operationId) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(operationId,
                (context) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        new UserEmail(command.getEmail()),
                        new UserPassword(command.getPassword()),
                        new ActivationCode(command.getActivationCode()),
                        new UserMobile(command.getCountryCode(), command.getMobileNumber()),
                        userId, context
                    );
                    return userId1.getDomainId();
                }, USER
            );

    }

    @AuditLog(actionName = MGMT_DELETE_USER)
    public void remove(String id, String changeId) {
        UserId userId = new UserId(id);
        User user = DomainRegistry.getUserRepository().get(userId);
        if (!DEFAULT_USERID.equals(user.getUserId().getDomainId())) {
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (context) -> {
                    DomainRegistry.getUserRepository().remove(user);
                    DomainRegistry.getAuditService()
                        .storeAuditAction(MGMT_DELETE_USER,
                            user);
                    DomainRegistry.getAuditService()
                        .logUserAction(log, MGMT_DELETE_USER,
                            user);
                    context.append(new UserDeleted(userId));
                    return null;
                }, USER);
        } else {
            throw new DefinedRuntimeException("default user cannot be deleted", "1021",
                HttpResponseCode.BAD_REQUEST);
        }
    }


    @AuditLog(actionName = MGMT_PATCH_USER)
    public void patch(String id, JsonPatch command, String changeId) {
        DomainRegistry.getAuditService()
            .logUserAction(log, MGMT_PATCH_USER,
                command);
        UserId userId = new UserId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                User user = DomainRegistry.getUserRepository().get(userId);
                UserPatchingCommand beforePatch = new UserPatchingCommand(user);
                UserPatchingCommand afterPatch =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .applyJsonPatch(command, beforePatch, UserPatchingCommand.class);
                user.lockUser(
                    afterPatch.getLocked(), context
                );
                return null;
            }, USER);
    }


    @AuditLog(actionName = MGMT_PATCH_BATCH_USER)
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        DomainRegistry.getAuditService()
            .logUserAction(log, MGMT_PATCH_BATCH_USER,
                commands);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getUserService().batchLock(commands, context);
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
                    .updatePassword(user, new CurrentPassword(command.getCurrentPwd()),
                        new UserPassword(command.getPassword()), context);
                DomainRegistry.getUserRepository().add(user);
                return null;
            }, USER);
    }

    public void forgetPassword(UserForgetPasswordCommand command, String changeId) {
        DomainRegistry.getAuditService()
            .logExternalUserAction(log, command.getEmail(), USER_FORGET_PWD);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getCoolDownService().hasCoolDown(command.getEmail(),
                    OperationType.PWD_RESET);
                DomainRegistry.getUserService()
                    .forgetPassword(new UserEmail(command.getEmail()), context);
                return null;
            }, USER);
    }

    public void resetPassword(UserResetPasswordCommand command, String changeId) {
        DomainRegistry.getAuditService()
            .logExternalUserAction(log, command.getEmail(), USER_RESET_PWD);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getUserService().resetPassword(new UserEmail(command.getEmail()),
                    new UserPassword(command.getNewPassword()),
                    new PasswordResetCode(command.getToken()), context);
                return null;
            }, USER);
    }


    public Optional<Image> queryProfileAvatar() {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        User user = DomainRegistry.getUserRepository().get(userId);
        if (user.getUserAvatar() != null) {
            String value = user.getUserAvatar().getValue();
            return ApplicationServiceRegistry.getImageApplicationService().queryById(value);
        } else {
            return Optional.empty();
        }
    }

    public ImageId createProfileAvatar(MultipartFile file, String changeId) {
        ImageId imageId =
            ApplicationServiceRegistry.getImageApplicationService().create(changeId, file);
        CommonDomainRegistry.getTransactionService()
            .transactionalEvent((context) -> {
                UserId userId = DomainRegistry.getCurrentUserService().getUserId();
                User user = DomainRegistry.getUserRepository().get(userId);
                user.setUserAvatar(new UserAvatar(imageId));
            });
        return imageId;
    }

    public LoginResult userLoginCheck(String ipAddress, String agentInfo,
                                      String username, @Nullable String mfaCode,
                                      @Nullable String mfaId) {
        UserEmail userEmail = new UserEmail(username);
        UserId userId =
            DomainRegistry.getUserRepository().getUserId(userEmail);
        log.debug("found user id {}", userId.getDomainId());
        boolean mfaRequired =
            DomainRegistry.getMfaService().isMfaRequired(userId, new UserSession(ipAddress));
        if (!mfaRequired) {
            log.debug("mfa not required, record current login information");
            recordLoginInfo(ipAddress, agentInfo, userId);
            return LoginResult.allow();
        } else {
            if (mfaCode != null) {
                log.debug("mfa code present");
                if (DomainRegistry.getMfaService().validateMfa(userId, mfaCode, mfaId)) {
                    log.debug("mfa required and check passed, record current login information");
                    recordLoginInfo(ipAddress, agentInfo, userId);
                    return LoginResult.allow();
                } else {
                    log.debug("mfa check failed");
                    return LoginResult.mfaMissMatch();
                }
            } else {
                log.debug("mfa required and need input by user");
                MfaId mfaId1 = CommonDomainRegistry.getTransactionService()
                    .returnedTransactionalEvent(
                        (context) -> DomainRegistry.getMfaService().triggerMfa(userId, context));
                return LoginResult
                    .mfaMissing(mfaId1);
            }
        }
    }


    @AuditLog(actionName = USER_UPDATE_PROFILE)
    public void updateProfile(UserUpdateProfileCommand command) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                User user = DomainRegistry.getUserRepository().get(userId);
                user.update(
                    new UserMobile(command.getCountryCode(), command.getMobileNumber()),
                    command.getUsername() != null ? new UserName(command.getUsername()) : null,
                    command.getLanguage()
                );
            }
        );
    }

    private void recordLoginInfo(String ipAddress, String agentInfo, UserId userId) {
        UserLoginRequest userLoginRequest =
            new UserLoginRequest(ipAddress, userId, agentInfo);
        CommonDomainRegistry.getTransactionService()
            .transactionalEvent(
                (context) -> DomainRegistry.getUserService().updateLastLogin(userLoginRequest));
    }

}
