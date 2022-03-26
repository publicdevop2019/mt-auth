package com.mt.access.domain.model.pending_user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.event.PendingUserCreated;

import com.mt.common.domain.CommonDomainRegistry;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PendingUserService {

    public RegistrationEmail createOrUpdatePendingUser(
            RegistrationEmail email,
            ActivationCode activationCode
    ) {
        Optional<PendingUser> pendingResourceOwner = DomainRegistry.getPendingUserRepository().pendingUserOfEmail(email);
        if (pendingResourceOwner.isEmpty()) {
            PendingUser pendingUser = new PendingUser(email, activationCode);
            DomainRegistry.getPendingUserRepository().add(pendingUser);
            CommonDomainRegistry.getDomainEventRepository().append(new PendingUserCreated(pendingUser.getRegistrationEmail()));
            return pendingUser.getRegistrationEmail();
        } else {
            pendingResourceOwner.get().newActivationCode(activationCode);
            return pendingResourceOwner.get().getRegistrationEmail();
        }
    }
}
