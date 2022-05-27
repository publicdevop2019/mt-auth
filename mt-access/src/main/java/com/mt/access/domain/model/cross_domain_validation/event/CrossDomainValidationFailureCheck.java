package com.mt.access.domain.model.cross_domain_validation.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.validate.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AuditEvent
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CrossDomainValidationFailureCheck extends DomainEvent {
    public static final String CROSS_DOMAIN_VALIDATION_FAILURE_CHECK =
        "cross_domain_validation_failure_check";
    public static final String name = "CROSS_DOMAIN_VALIDATION_FAILURE_CHECK";
    private String email;

    public CrossDomainValidationFailureCheck(String email) {
        super();
        Validator.isEmail(email);
        setTopic(CROSS_DOMAIN_VALIDATION_FAILURE_CHECK);
        setName(name);
        this.email = email;
    }
}
