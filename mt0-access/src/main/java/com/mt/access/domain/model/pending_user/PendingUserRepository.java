package com.mt.access.domain.model.pending_user;

import java.util.Optional;

public interface PendingUserRepository {
    Optional<PendingUser> pendingUserOfEmail(RegistrationEmail email);

    void add(PendingUser pendingUser);
}
