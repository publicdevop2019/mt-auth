package com.mt.access.domain.model.pending_user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PendingUser extends Auditable {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private RegistrationEmail registrationEmail;

    @Getter
    private ActivationCode activationCode;

    public PendingUser(RegistrationEmail registrationEmail, ActivationCode activationCode) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setRegistrationEmail(registrationEmail);
        setActivationCode(activationCode);
        DomainRegistry.getPendingUserValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    public static PendingUser fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                              Long modifiedAt, String modifiedBy,
                                              Integer version,
                                              ActivationCode activationCode,
                                              RegistrationEmail domainId) {
        PendingUser pendingUser = new PendingUser();
        pendingUser.setId(id);
        pendingUser.setCreatedAt(createdAt);
        pendingUser.setCreatedBy(createdBy);
        pendingUser.setModifiedAt(modifiedAt);
        pendingUser.setModifiedBy(modifiedBy);
        pendingUser.setVersion(version);
        pendingUser.setActivationCode(activationCode);
        pendingUser.setRegistrationEmail(domainId);
        return pendingUser;
    }

    private void setActivationCode(ActivationCode activationCode) {
        this.activationCode = activationCode;
    }
}
