package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginHistoryRepository;
import com.mt.access.domain.model.user.UserId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaLoginHistoryRepository extends JpaRepository<LoginHistory, Long>,
    LoginHistoryRepository {
    default void add(LoginHistory info) {
        save(info);
    }

    default Set<LoginHistory> getLast100Login(UserId userId) {
        return new HashSet<>(findTop100ByUserId(userId));
    }


    List<LoginHistory> findTop100ByUserId(UserId u);
}
