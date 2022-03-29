package com.mt.access.port.adapter.persistence.pending_user;

import com.mt.access.domain.model.pending_user.PendingUser;
import com.mt.access.domain.model.pending_user.PendingUserRepository;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaPendingUserRepository
    extends JpaRepository<PendingUser, Long>, PendingUserRepository {
    default Optional<PendingUser> pendingUserOfEmail(RegistrationEmail email) {
        return findByRegistrationEmailEmail(email.getEmail());
    }

    Optional<PendingUser> findByRegistrationEmailEmail(String email);

    default void add(PendingUser pendingUser) {
        save(pendingUser);
    }

}
