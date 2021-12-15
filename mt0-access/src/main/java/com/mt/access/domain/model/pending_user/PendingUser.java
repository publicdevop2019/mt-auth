package com.mt.access.domain.model.pending_user;

import com.google.common.base.Objects;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

@Entity
@Table
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,region = "pendingUserRegion")
public class PendingUser extends Auditable {
    @Id
    @Setter(AccessLevel.PRIVATE)
    private Long id;

    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
    private RegistrationEmail registrationEmail;

    @Column
    @Getter
    @Embedded
    private ActivationCode activationCode;

    public PendingUser(RegistrationEmail registrationEmail, ActivationCode activationCode) {
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setRegistrationEmail(registrationEmail);
        setActivationCode(activationCode);
        DomainRegistry.getPendingUserValidationService().validate(this, new HttpValidationNotificationHandler());
    }

    private void setActivationCode(ActivationCode activationCode) {
        this.activationCode = activationCode;
        DomainEventPublisher.instance().publish(new PendingUserActivationCodeUpdated(registrationEmail, activationCode));
    }

    public void newActivationCode(ActivationCode activationCode) {
        DomainRegistry.getPendingUserValidationService().validate(this, new HttpValidationNotificationHandler());
        setActivationCode(activationCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PendingUser)) return false;
        if (!super.equals(o)) return false;
        PendingUser that = (PendingUser) o;
        return Objects.equal(registrationEmail, that.registrationEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), registrationEmail);
    }
}
