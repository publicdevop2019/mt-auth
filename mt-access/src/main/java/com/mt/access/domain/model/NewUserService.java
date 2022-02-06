package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserPassword;
import com.mt.access.domain.model.user.event.UserCreated;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NewUserService {
    public UserId create(UserEmail userEmail, UserPassword password, ActivationCode activationCode, UserId userId) {
        Optional<PendingUser> pendingUser = DomainRegistry.getPendingUserRepository().pendingUserOfEmail(new RegistrationEmail(userEmail.getEmail()));
        if (pendingUser.isPresent()) {
            if (pendingUser.get().getActivationCode() == null || !pendingUser.get().getActivationCode().getActivationCode().equals(activationCode.getActivationCode()))
                throw new IllegalArgumentException("activation code mismatch");
            User user = new User(userEmail, password, userId);
            DomainRegistry.getUserRepository().add(user);
            DomainEventPublisher.instance().publish(new UserCreated(user.getUserId()));
            return user.getUserId();
        } else {
            return null;
        }
    }
}
