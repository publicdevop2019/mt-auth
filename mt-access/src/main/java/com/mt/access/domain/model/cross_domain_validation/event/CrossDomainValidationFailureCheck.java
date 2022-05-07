package com.mt.access.domain.model.cross_domain_validation.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class CrossDomainValidationFailureCheck extends DomainEvent implements AuditEvent {
    public static final String CROSS_DOMAIN_VALIDATION_FAILURE_CHECK =
        "cross_domain_validation_failure_check";
    public static final String name = "CROSS_DOMAIN_VALIDATION_FAILURE_CHECK";

    public CrossDomainValidationFailureCheck() {
        super();
        setTopic(CROSS_DOMAIN_VALIDATION_FAILURE_CHECK);
        setName(name);
    }
}
