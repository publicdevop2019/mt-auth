package com.mt.access.domain.model.cross_domain_validation;

import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.local_transaction.TransactionContext;
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

    public ValidationResult reset() {
        ValidationResult validationResult =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, ValidationResult.class);
        validationResult.failureCount = 0;
        validationResult.notifyAdmin = false;
        return validationResult;
    }

    public ValidationResult increaseFailureCount(TransactionContext context, String adminEmail) {
        ValidationResult validationResult =
            CommonDomainRegistry.getCustomObjectSerializer().deepCopy(this, ValidationResult.class);
        validationResult.failureCount = validationResult.failureCount + 1;
        if (failedTooManyTimes() && !validationResult.notifyAdmin) {
            validationResult.notifyAdmin = true;
            context
                .append(new CrossDomainValidationFailureCheck(adminEmail));
        }
        return validationResult;
    }

    public boolean failedTooManyTimes() {
        return failureCount > 2;
    }
}
