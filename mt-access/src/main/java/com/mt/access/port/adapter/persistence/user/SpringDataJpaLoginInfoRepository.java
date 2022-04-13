package com.mt.access.port.adapter.persistence.user;

import com.mt.access.domain.model.user.LoginInfo;
import com.mt.access.domain.model.user.LoginInfoRepository;
import com.mt.access.domain.model.user.UserId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaLoginInfoRepository extends JpaRepository<LoginInfo, Long>,
    LoginInfoRepository {
    default Optional<LoginInfo> ofId(UserId userId) {
        return findByUserId(userId);
    }

    default void add(LoginInfo info) {
        save(info);
    }

    Optional<LoginInfo> findByUserId(UserId u);
}
