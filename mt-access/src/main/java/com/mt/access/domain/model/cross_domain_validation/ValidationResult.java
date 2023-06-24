package com.mt.access.domain.model.cross_domain_validation;

import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table
public class ValidationResult {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    private Long id;

    @Column(name = "failure_count", columnDefinition = "TINYINT")
    private Integer failureCount = 0;
    @Column(name = "notify_admin", columnDefinition = "BIT", length = 1)
    private Boolean notifyAdmin = false;

    private ValidationResult() {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    }

    public static ValidationResult create() {
        return new ValidationResult();
    }

    public void resetFailureCount() {
        failureCount = 0;
        notifyAdmin = false;
    }

    public void increaseFailureCount() {
        failureCount = failureCount + 1;
    }

    public boolean shouldPause() {
        return failureCount > 2;
    }

    public boolean hasNotified() {
        return notifyAdmin;
    }

    public void markAsNotified() {
        notifyAdmin = true;
    }
}
