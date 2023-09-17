package com.mt.access.domain.model.cross_domain_validation;

import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class ValidationResult {
    @Setter(AccessLevel.PROTECTED)
    @Getter
    private Long id;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Integer failureCount = 0;
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private Boolean notifyAdmin = false;

    private ValidationResult() {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    }

    public static ValidationResult create() {
        return new ValidationResult();
    }

    public static ValidationResult fromDatabaseRow(Long id, Integer failureCount,
                                                   Boolean notifyAdmin) {
        ValidationResult result = new ValidationResult();
        result.setId(id);
        result.setFailureCount(failureCount);
        result.setNotifyAdmin(notifyAdmin);
        return result;
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
