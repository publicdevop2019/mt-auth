package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.LoginHistory;
import com.mt.access.domain.model.user.LoginHistoryRepository;
import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.LoginInfoRepository;
import com.mt.access.domain.model.user.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaLoginHistoryRepository extends JpaRepository<LoginHistory, Long>,
    LoginHistoryRepository {
    default Optional<LoginHistory> ofId(UserId userId) {
        return findByUserId(userId);
    }

    default void add(LoginHistory info) {
        save(info);
    }

    Optional<LoginHistory> findByUserId(UserId u);
}
