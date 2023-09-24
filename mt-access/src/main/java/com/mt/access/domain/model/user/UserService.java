package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.PatchCommand;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void updatePassword(User user, CurrentPassword currentPwd, UserPassword password,
                               TransactionContext context) {
        if (!DomainRegistry.getEncryptionService()
            .compare(currentPwd.getRawPassword(), user.getPassword().getPassword())) {
            throw new DefinedRuntimeException("wrong password", "1000",
                HttpResponseCode.BAD_REQUEST);
        }
        user.setPassword(password);
        DomainRegistry.getUserRepository().add(user);
        context
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void forgetPassword(UserEmail email, TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(email);
        PasswordResetCode passwordResetToken = new PasswordResetCode();
        user.setPwdResetToken(passwordResetToken, context);
        DomainRegistry.getUserRepository().add(user);

    }

    public void resetPassword(UserEmail email, UserPassword newPassword, PasswordResetCode token,
                              TransactionContext context) {
        User user = DomainRegistry.getUserRepository().get(email);
        if (user.getPwdResetToken() == null) {
            throw new DefinedRuntimeException("token not exist", "1003",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!user.getPwdResetToken().equals(token)) {
            throw new DefinedRuntimeException("token mismatch", "1004",
                HttpResponseCode.BAD_REQUEST);
        }
        user.setPassword(newPassword);
        DomainRegistry.getUserRepository().add(user);
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
