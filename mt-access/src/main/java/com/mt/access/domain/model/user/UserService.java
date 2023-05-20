package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.PatchCommand;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void updatePassword(User user, CurrentPassword currentPwd, UserPassword password) {
        if (!DomainRegistry.getEncryptionService().compare(user.getPassword(), currentPwd)) {
            throw new DefinedRuntimeException("wrong password", "1000",
                HttpResponseCode.BAD_REQUEST);
        }
        user.setPassword(password);
        DomainRegistry.getUserRepository().add(user);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void forgetPassword(UserEmail email) {
        User user = DomainRegistry.getUserRepository().get(email);
        PasswordResetCode passwordResetToken = new PasswordResetCode();
        user.setPwdResetToken(passwordResetToken);
        DomainRegistry.getUserRepository().add(user);

    }

    public void resetPassword(UserEmail email, UserPassword newPassword, PasswordResetCode token) {
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
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void batchLock(List<PatchCommand> commands) {
        if (Boolean.TRUE.equals(commands.get(0).getValue())) {
            commands.stream().map(e -> new UserId(e.getPath().split("/")[1])).forEach(e -> {
                CommonDomainRegistry.getDomainEventRepository().append(new UserGetLocked(e));
            });
        }
        DomainRegistry.getUserRepository().batchLock(commands);
    }

    public void updateLastLogin(UserLoginRequest command) {
        UserId userId = command.getUserId();
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().query(userId);
        loginInfo.ifPresentOrElse(e -> e.updateLastLogin(command), () -> {
            LoginInfo loginInfo1 = new LoginInfo(command);
            DomainRegistry.getLoginInfoRepository().add(loginInfo1);
        });
        LoginHistory loginHistory = new LoginHistory(command);
        DomainRegistry.getLoginHistoryRepository().add(loginHistory);
    }


}
