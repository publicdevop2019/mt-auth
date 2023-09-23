package com.mt.access.domain.model.cross_domain_validation;

import java.util.Optional;

public interface ValidationResultRepository {
    Optional<ValidationResult> query();

    void create(ValidationResult result);

    void update(ValidationResult result);
}
