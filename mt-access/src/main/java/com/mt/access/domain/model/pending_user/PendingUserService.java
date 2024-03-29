package com.mt.access.domain.model.pending_user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.pending_user.event.PendingUserCreated;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PendingUserService {

    public RegistrationEmail createOrUpdatePendingUser(
        ClientId clientId, RegistrationEmail email,
        ActivationCode activationCode, TransactionContext context
    ) {
        Optional<PendingUser> pendingResourceOwner =
            DomainRegistry.getPendingUserRepository().query(email);
        if (pendingResourceOwner.isEmpty()) {
            PendingUser pendingUser = new PendingUser(email, activationCode);
            DomainRegistry.getPendingUserRepository().add(clientId, pendingUser);
            context
                .append(new PendingUserCreated(pendingUser.getRegistrationEmail()));
            context
                .append(new PendingUserActivationCodeUpdated(email, activationCode));
            return pendingUser.getRegistrationEmail();
        } else {
            DomainRegistry.getPendingUserRepository().updateActivationCode(clientId, email, activationCode);
            return pendingResourceOwner.get().getRegistrationEmail();
        }
    }
}
