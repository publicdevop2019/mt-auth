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
    default Optional<PendingUser> by(RegistrationEmail email) {
        return findByRegistrationEmailDomainId(email.getDomainId());
    }

    //Spring data jpa cannot work if override domainId in registration email to email
    Optional<PendingUser> findByRegistrationEmailDomainId(String email);

    default void add(PendingUser pendingUser) {
        save(pendingUser);
    }

}
