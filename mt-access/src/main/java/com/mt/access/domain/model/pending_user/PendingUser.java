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
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "pendingUserRegion")
@Table
@EqualsAndHashCode(callSuper = true)
public class PendingUser extends Auditable {
    @Column(unique = true)
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private RegistrationEmail registrationEmail;

    @Column
    @Getter
    @Embedded
    private ActivationCode activationCode;

    public PendingUser(RegistrationEmail registrationEmail, ActivationCode activationCode, TransactionContext context) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setRegistrationEmail(registrationEmail);
        setActivationCode(activationCode,context);
        DomainRegistry.getPendingUserValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setActivationCode(ActivationCode activationCode, TransactionContext context) {
        this.activationCode = activationCode;
        context
            .append(new PendingUserActivationCodeUpdated(registrationEmail, activationCode));
    }

    public void newActivationCode(ActivationCode activationCode, TransactionContext context) {
        DomainRegistry.getPendingUserValidationService()
            .validate(this, new HttpValidationNotificationHandler());
        setActivationCode(activationCode,context);
    }

}
