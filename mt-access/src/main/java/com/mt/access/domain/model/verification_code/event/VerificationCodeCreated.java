package com.mt.access.domain.model.verification_code.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class VerificationCodeCreated extends DomainEvent {
    public static final String name = "VERIFICATION_CODE_CREATED";
    public static final String VERIFICATION_CODE_CREATED = "verification_code_created";

    {
        setName(name);
        setTopic(VERIFICATION_CODE_CREATED);

    }

    public VerificationCodeCreated(RegistrationEmail email) {

        super(email);
    }

    public VerificationCodeCreated(RegistrationMobile mobile) {
        super(mobile);
    }
}
