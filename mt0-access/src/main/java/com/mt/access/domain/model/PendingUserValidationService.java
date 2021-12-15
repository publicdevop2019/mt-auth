package com.mt.access.domain.model;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PendingUserValidationService {
    public void validate(PendingUser pendingUser, ValidationNotificationHandler handler) {
        Optional<User> user = DomainRegistry.getUserRepository().searchExistingUserWith(new UserEmail(pendingUser.getRegistrationEmail().getEmail()));
        if (user.isPresent())
            handler.handleError("already an user " + pendingUser.getRegistrationEmail().getEmail());
    }
}
