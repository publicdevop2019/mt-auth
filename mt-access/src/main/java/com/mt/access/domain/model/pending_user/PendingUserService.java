package com.mt.access.domain.model.pending_user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.event.PendingUserCreated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class PendingUserService {

    public RegistrationEmail createOrUpdatePendingUser(
        RegistrationEmail email,
        ActivationCode activationCode, TransactionContext context
    ) {
        Optional<PendingUser> pendingResourceOwner =
            DomainRegistry.getPendingUserRepository().query(email);
        if (pendingResourceOwner.isEmpty()) {
            PendingUser pendingUser = new PendingUser(email, activationCode,context);
            DomainRegistry.getPendingUserRepository().add(pendingUser);
            context
                .append(new PendingUserCreated(pendingUser.getRegistrationEmail()));
            return pendingUser.getRegistrationEmail();
        } else {
            pendingResourceOwner.get().newActivationCode(activationCode,context);
            return pendingResourceOwner.get().getRegistrationEmail();
        }
    }
}
