package com.mt.access.domain.model.user;

import java.util.Optional;

public interface LoginInfoRepository {
    Optional<LoginInfo> query(UserId userId);

    void add(LoginInfo info);
}
