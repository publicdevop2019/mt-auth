package com.mt.access.domain.model.pending_user;

import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.client.ClientId;
import java.util.Optional;

public interface PendingUserRepository {
    Optional<PendingUser> query(RegistrationEmail email);

    void add(ClientId clientId, PendingUser pendingUser);

    void updateActivationCode(ClientId clientId, RegistrationEmail email,
                              ActivationCode activationCode);
}
