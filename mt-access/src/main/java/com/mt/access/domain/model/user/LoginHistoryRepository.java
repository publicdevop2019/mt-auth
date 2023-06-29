package com.mt.access.domain.model.user;

import java.util.Set;

public interface LoginHistoryRepository {

    void add(LoginHistory info);

    Set<LoginHistory> getLast100Login(UserId userId);
}
