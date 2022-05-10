package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.user.User;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PendingUserValidationService {
    public void validate(PendingUser pendingUser, ValidationNotificationHandler handler) {
        Optional<User> user = DomainRegistry.getUserRepository()
            .searchExistingUserWith(new UserEmail(pendingUser.getRegistrationEmail().getDomainId()));
        if (user.isPresent()) {
            handler.handleError("already an user " + pendingUser.getRegistrationEmail().getDomainId());
        }
    }
}
