package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import java.util.Optional;
import javax.annotation.Nullable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void updatePassword(User user, @Nullable CurrentPassword currentPwd,
                               UserPassword password,
                               TransactionContext context) {
        if (Checker.notNull(user.getPassword())) {
            if (!DomainRegistry.getEncryptionService()
                .compare(Checker.notNull(currentPwd) ? currentPwd.getRawPassword() : null,
                    user.getPassword().getPassword())) {
                throw new DefinedRuntimeException("wrong password", "1000",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        User updated = user.updatePassword(password);
        DomainRegistry.getUserRepository().update(user, updated);
        context
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void forgetPassword(UserEmail email, TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(email);
        PasswordResetCode passwordResetToken = new PasswordResetCode();
        User user1 = user.setPwdResetToken(passwordResetToken, email, context);
        DomainRegistry.getUserRepository().update(user, user1);

    }


    public void forgetPassword(UserMobile mobile, TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(mobile);
        PasswordResetCode passwordResetToken = new PasswordResetCode();
        User user1 = user.setPwdResetToken(passwordResetToken, mobile, context);
        DomainRegistry.getUserRepository().update(user, user1);
    }

    public void resetPassword(UserEmail email, UserPassword newPassword, PasswordResetCode token,
                              TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(email);
        updatePwd(newPassword, token, context, user);
    }

    public void resetPassword(UserMobile mobile, UserPassword newPassword, PasswordResetCode token,
                              TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(mobile);
        updatePwd(newPassword, token, context, user);
    }

    private static void updatePwd(UserPassword newPassword, PasswordResetCode token,
                                  TransactionContext context, User user) {
        if (user.getPwdResetToken() == null) {
            throw new DefinedRuntimeException("token not exist", "1003",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!user.getPwdResetToken().equals(token)) {
            throw new DefinedRuntimeException("token mismatch", "1004",
                HttpResponseCode.BAD_REQUEST);
        }
        User user1 = user.updatePassword(newPassword);
        DomainRegistry.getUserRepository().update(user, user1);
        context
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void updateLastLogin(UserLoginRequest command, ProjectId loginProjectId) {
        UserId userId = command.getUserId();
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().query(userId);
        loginInfo.ifPresentOrElse(
            e -> DomainRegistry.getLoginInfoRepository().updateLastLogin(command, userId),
            () -> {
                LoginInfo info = new LoginInfo(command);
                DomainRegistry.getLoginInfoRepository().add(info);
            });
        LoginHistory loginHistory = new LoginHistory(command, loginProjectId);
        DomainRegistry.getLoginHistoryRepository().add(loginHistory);
    }

}
