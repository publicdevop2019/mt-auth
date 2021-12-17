package com.mt.access.domain.model.user;

import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.restful.PatchCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.access.domain.model.user.*;
import com.mt.access.domain.model.user.event.UserCreated;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    public void updatePassword(User user, CurrentPassword currentPwd, UserPassword password) {
        if (!DomainRegistry.getEncryptionService().compare(user.getPassword(), currentPwd))
            throw new IllegalArgumentException("wrong password");
        user.setPassword(password);
        DomainRegistry.getUserRepository().add(user);
        DomainEventPublisher.instance().publish(new UserPasswordChanged(user.getUserId()));
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
        if (user.get().getPwdResetToken() == null)
            throw new IllegalArgumentException("token not exist");
        if (!user.get().getPwdResetToken().equals(token))
            throw new IllegalArgumentException("token mismatch");
        user.get().setPassword(newPassword);
        DomainRegistry.getUserRepository().add(user.get());
        DomainEventPublisher.instance().publish(new UserPasswordChanged(user.get().getUserId()));
    }

    public void batchLock(List<PatchCommand> commands) {
        if (Boolean.TRUE.equals(commands.get(0).getValue())) {
            commands.stream().map(e -> new UserId(e.getPath().split("/")[1])).forEach(e -> {
                DomainEventPublisher.instance().publish(new UserGetLocked(e));
            });
        }
        DomainRegistry.getUserRepository().batchLock(commands);
    }
}
