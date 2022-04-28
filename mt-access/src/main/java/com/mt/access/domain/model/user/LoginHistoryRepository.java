package com.mt.access.domain.model.user;

import java.util.Optional;

public interface LoginHistoryRepository {
    Optional<LoginHistory> ofId(UserId userId);

    void add(LoginHistory info);
}
