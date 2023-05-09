package com.mt.access.domain.model.pending_user;

import java.util.Optional;

public interface PendingUserRepository {
    Optional<PendingUser> query(RegistrationEmail email);

    void add(PendingUser pendingUser);
}
