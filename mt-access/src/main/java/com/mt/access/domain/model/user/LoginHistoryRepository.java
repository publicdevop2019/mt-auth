package com.mt.access.domain.model.user;

import java.util.Optional;
import java.util.Set;

public interface LoginHistoryRepository {
    Optional<LoginHistory> ofId(UserId userId);

    void add(LoginHistory info);

    Set<LoginHistory> getLast100Login(UserId userId);
}
