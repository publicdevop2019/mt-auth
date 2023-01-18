package com.mt.access.application.user;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.UpdateUserCommand;
import com.mt.access.application.user.command.UserCreateCommand;
import com.mt.access.application.user.command.UserForgetPasswordCommand;
import com.mt.access.application.user.command.UserPatchingCommand;
import com.mt.access.application.user.command.UserResetPasswordCommand;
import com.mt.access.application.user.command.UserUpdateBizUserPasswordCommand;
import com.mt.access.application.user.command.UserUpdateProfileCommand;
import com.mt.access.application.user.representation.UserProfileRepresentation;
import com.mt.access.application.user.representation.UserSpringRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.image.Image;
import com.mt.access.domain.model.image.ImageId;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.LoginResult;
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
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class UserApplicationService implements UserDetailsService {

    public static final String USER = "User";
    public static final String DEFAULT_USERID = "0U8AZTODP4H0";

    @Transactional
    public String create(UserCreateCommand command, String operationId) {
        UserId userId = new UserId();
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(operationId,
                (change) -> {
                    UserId userId1 = DomainRegistry.getNewUserService().create(
                        new UserEmail(command.getEmail()),
                        new UserPassword(command.getPassword()),
                        new ActivationCode(command.getActivationCode()),
                        new UserMobile(command.getCountryCode(), command.getMobileNumber()),
                        userId
                    );
                    return userId1.getDomainId();
                }, USER
            );

    }

    public SumPagedRep<User> users(String queryParam, String pageParam, String config) {
        return DomainRegistry.getUserRepository()
            .usersOfQuery(new UserQuery(queryParam, pageParam, config));
    }

    public Set<User> users(Set<UserId> userIdSet) {
        return QueryUtility.getAllByQuery(e -> DomainRegistry.getUserRepository()
            .usersOfQuery(e), new UserQuery(userIdSet));
    }

    public Optional<User> user(String id) {
        return DomainRegistry.getUserRepository().userOfId(new UserId(id));
    }


    @Transactional
    @AuditLog(actionName = "lock user")
    public void adminLock(String id, UpdateUserCommand command, String changeId) {
        DomainRegistry.getAuditService()
            .logAdminAction(DomainRegistry.getCurrentUserService().getUserId(), "lock user",
                "with user id :" + id);
        UserId userId = new UserId(id);
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (ignored) -> {
                    user1.lockUser(
                        command.isLocked()
                    );
                    return null;
                }, USER);
            DomainRegistry.getUserRepository().add(user1);
        }
    }


    @Transactional
    @AuditLog(actionName = "lock user")
    public void delete(String id, String changeId) {
        DomainRegistry.getAuditService()
            .logAdminAction(DomainRegistry.getCurrentUserService().getUserId(), "delete user",
                "with user id :" + id);
        UserId userId = new UserId(id);
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            if (!DEFAULT_USERID.equals(user1.getUserId().getDomainId())) {
                CommonApplicationServiceRegistry.getIdempotentService()
                    .idempotent(changeId, (ignored) -> {
                        DomainRegistry.getUserRepository().remove(user1);
                        return null;
                    }, USER);
                CommonDomainRegistry.getDomainEventRepository().append(new UserDeleted(userId));
            } else {
                throw new DefaultUserDeleteException();
            }
        }
    }


    @Transactional
    @AuditLog(actionName = "patch user")
    public void patch(String id, JsonPatch command, String changeId) {
        DomainRegistry.getAuditService()
            .logAdminAction(DomainRegistry.getCurrentUserService().getUserId(), "patch user",
                "with user id :" + id);
        UserId userId = new UserId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
                if (user.isPresent()) {
                    User original = user.get();
                    UserPatchingCommand beforePatch = new UserPatchingCommand(original);
                    UserPatchingCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, UserPatchingCommand.class);
                    original.lockUser(
                        afterPatch.isLocked()
                    );
                }
                return null;
            }, USER);
    }


    @Transactional
    @AuditLog(actionName = "patch many user")
    public void patchBatch(List<PatchCommand> commands, String changeId) {
        DomainRegistry.getAuditService()
            .logAdminAction(DomainRegistry.getCurrentUserService().getUserId(), "patch many use",
                commands.toString());
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                DomainRegistry.getUserService().batchLock(commands);
                return null;
            }, USER);
    }


    @Transactional
    @AuditLog(actionName = "update password")
    public void updatePassword(UserUpdateBizUserPasswordCommand command, String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isPresent()) {
            User user1 = user.get();
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (ignored) -> {
                    DomainRegistry.getUserService()
                        .updatePassword(user1, new CurrentPassword(command.getCurrentPwd()),
                            new UserPassword(command.getPassword()));
                    return null;
                }, USER);
            DomainRegistry.getUserRepository().add(user1);
        }
    }


    @Transactional
    public void forgetPassword(UserForgetPasswordCommand command, String changeId) {
        DomainRegistry.getAuditService()
            .logUserAction(command.getEmail(), "forget password");
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                DomainRegistry.getCoolDownService().hasCoolDown(command.getEmail(),
                    OperationType.PWD_RESET);
                DomainRegistry.getUserService().forgetPassword(new UserEmail(command.getEmail()));
                return null;
            }, USER);
    }


    @Transactional
    public void resetPassword(UserResetPasswordCommand command, String changeId) {
        DomainRegistry.getAuditService()
            .logUserAction(command.getEmail(), "reset password");
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (ignored) -> {
                DomainRegistry.getUserService().resetPassword(new UserEmail(command.getEmail()),
                    new UserPassword(command.getNewPassword()),
                    new PasswordResetCode(command.getToken()));
                return null;
            }, USER);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> client;
        if (Validator.isValidEmail(username)) {
            //for login
            client =
                DomainRegistry.getUserRepository().searchExistingUserWith(new UserEmail(username));
        } else {
            //for refresh token
            client = DomainRegistry.getUserRepository().userOfId(new UserId(username));
        }
        return client.map(UserSpringRepresentation::new).orElse(null);
    }

    private void updateLastLoginInfo(UserLoginRequest command) {
        CommonDomainRegistry.getTransactionService().transactional(()-> DomainRegistry.getUserService().updateLastLogin(command));
    }

    public Optional<Image> profileAvatar() {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        return user.flatMap((e) -> {
            if (e.getUserAvatar() != null) {
                String value = e.getUserAvatar().getValue();
                return ApplicationServiceRegistry.getImageApplicationService().queryById(value);
            } else {
                return Optional.empty();
            }
        });
    }

    @Transactional
    @AuditLog(actionName = "create avatar")
    public ImageId createProfileAvatar(MultipartFile file, String changeId) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("cannot find user " + userId);
        }
        ImageId imageId =
            ApplicationServiceRegistry.getImageApplicationService().create(changeId, file);
        user.get().setUserAvatar(new UserAvatar(imageId));
        return imageId;
    }

    public LoginResult userLogin(String ipAddress, String agentInfo, String grantType,
                                 String username, @Nullable String mfa, @Nullable String mfaId) {
        if ("password".equalsIgnoreCase(grantType) && username != null) {
            UserEmail userEmail = new UserEmail(username);
            Optional<User> user =
                DomainRegistry.getUserRepository().searchExistingUserWith(userEmail);
            if (user.isEmpty()) {
                throw new InternalAuthenticationServiceException("user not found");
            }
            UserId userId = user.get().getUserId();
            boolean mfaRequired =
                DomainRegistry.getMfaService().isMfaRequired(userId, new UserSession(ipAddress));
            if (!mfaRequired) {
                //if mfa not required, record current login info
                recordLoginInfo(ipAddress, agentInfo, userId);
                return LoginResult.allow();
            } else {
                if (mfa != null) {
                    if (DomainRegistry.getMfaService().validateMfa(userId, mfa, mfaId)) {
                        recordLoginInfo(ipAddress, agentInfo, userId);
                        return LoginResult.allow();
                    } else {
                        return LoginResult.mfaMissMatch();
                    }
                } else {
                    MfaId execute=CommonDomainRegistry.getTransactionService().returnedTransactional(()-> DomainRegistry.getMfaService().triggerMfa(userId));
                    return LoginResult
                        .mfaMissing(execute);
                }
            }
        }
        return LoginResult.allow();
    }

    private void recordLoginInfo(String ipAddress, String agentInfo, UserId userId) {
        UserLoginRequest userLoginRequest =
            new UserLoginRequest(ipAddress, userId, agentInfo);
        updateLastLoginInfo(userLoginRequest);
    }

    public Optional<UserProfileRepresentation> myProfile() {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().ofId(userId);
        return user.flatMap((e) -> {
            UserProfileRepresentation userProfileRepresentation =
                new UserProfileRepresentation(e, loginInfo.get());
            return Optional.of(userProfileRepresentation);
        });
    }

    @Transactional
    @AuditLog(actionName = "update profile")
    public void updateProfile(UserUpdateProfileCommand command) {
        UserId userId = DomainRegistry.getCurrentUserService().getUserId();
        Optional<User> user = DomainRegistry.getUserRepository().userOfId(userId);
        user.ifPresent(e -> e.update(
            new UserMobile(command.getCountryCode(), command.getMobileNumber()),
            command.getUsername() != null ? new UserName(command.getUsername()) : null,
            command.getLanguage()));
    }

    public static class DefaultUserDeleteException extends RuntimeException {
    }

}
