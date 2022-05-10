package com.mt.access.domain.model.pending_user;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Entity
@NoArgsConstructor
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE,
    region = "pendingUserRegion")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"domainId", "deleted"}))
public class PendingUser extends Auditable {
    @Column
    @Setter(AccessLevel.PRIVATE)
    @Getter
    @Embedded
//    @AttributeOverrides({
//        @AttributeOverride(name = "domainId", column = @Column(name = "email"))
//    })
    private RegistrationEmail registrationEmail;

    @Column
    @Getter
    @Embedded
    private ActivationCode activationCode;

    public PendingUser(RegistrationEmail registrationEmail, ActivationCode activationCode) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setRegistrationEmail(registrationEmail);
        setActivationCode(activationCode);
        DomainRegistry.getPendingUserValidationService()
            .validate(this, new HttpValidationNotificationHandler());
    }

    private void setActivationCode(ActivationCode activationCode) {
        this.activationCode = activationCode;
        CommonDomainRegistry.getDomainEventRepository()
            .append(new PendingUserActivationCodeUpdated(registrationEmail, activationCode));
    }

    public void newActivationCode(ActivationCode activationCode) {
        DomainRegistry.getPendingUserValidationService()
            .validate(this, new HttpValidationNotificationHandler());
        setActivationCode(activationCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PendingUser)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        PendingUser that = (PendingUser) o;
        return Objects.equal(registrationEmail, that.registrationEmail);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), registrationEmail);
    }
}
