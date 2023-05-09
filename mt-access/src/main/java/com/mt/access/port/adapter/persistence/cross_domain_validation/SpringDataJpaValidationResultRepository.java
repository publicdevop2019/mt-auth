package com.mt.access.port.adapter.persistence.cross_domain_validation;

import com.mt.access.domain.model.cross_domain_validation.ValidationResult;
import com.mt.access.domain.model.cross_domain_validation.ValidationResultRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaValidationResultRepository extends ValidationResultRepository,
    JpaRepository<ValidationResult, Long> {
    default void add(ValidationResult result) {
        save(result);
    }

    default Optional<ValidationResult> query() {
        return findAll().stream().findAny();
    }

}
