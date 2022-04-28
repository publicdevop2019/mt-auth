package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.PatchCommand;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void updatePassword(User user, CurrentPassword currentPwd, UserPassword password) {
        if (!DomainRegistry.getEncryptionService().compare(user.getPassword(), currentPwd)) {
            throw new IllegalArgumentException("wrong password");
        }
        user.setPassword(password);
        DomainRegistry.getUserRepository().add(user);
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPasswordChanged(user.getUserId()));
    }

    public void forgetPassword(UserEmail email) {
        Optional<User> user = DomainRegistry.getUserRepository().searchExistingUserWith(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user does not exist");
        }
        PasswordResetCode passwordResetToken = new PasswordResetCode();
        user.get().setPwdResetToken(passwordResetToken);
        DomainRegistry.getUserRepository().add(user.get());

    }

    public void resetPassword(UserEmail email, UserPassword newPassword, PasswordResetCode token) {
        Optional<User> user = DomainRegistry.getUserRepository().searchExistingUserWith(email);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("user does not exist");
        }
        if (user.get().getPwdResetToken() == null) {
            throw new IllegalArgumentException("token not exist");
        }
        if (!user.get().getPwdResetToken().equals(token)) {
            throw new IllegalArgumentException("token mismatch");
        }
        user.get().setPassword(newPassword);
        DomainRegistry.getUserRepository().add(user.get());
        CommonDomainRegistry.getDomainEventRepository()
            .append(new UserPasswordChanged(user.get().getUserId()));
    }

    public void batchLock(List<PatchCommand> commands) {
        if (Boolean.TRUE.equals(commands.get(0).getValue())) {
            commands.stream().map(e -> new UserId(e.getPath().split("/")[1])).forEach(e -> {
                CommonDomainRegistry.getDomainEventRepository().append(new UserGetLocked(e));
            });
        }
        DomainRegistry.getUserRepository().batchLock(commands);
    }

    public void updateLastLogin(UpdateLoginInfoCommand command) {
        UserId userId = command.getUserId();
        Optional<LoginInfo> loginInfo = DomainRegistry.getLoginInfoRepository().ofId(userId);
        loginInfo.ifPresentOrElse(e -> e.updateLastLogin(command), () -> {
            LoginInfo loginInfo1 = new LoginInfo(command);
            DomainRegistry.getLoginInfoRepository().add(loginInfo1);
        });
        LoginHistory loginHistory = new LoginHistory(command);
        DomainRegistry.getLoginHistoryRepository().add(loginHistory);
    }
}
