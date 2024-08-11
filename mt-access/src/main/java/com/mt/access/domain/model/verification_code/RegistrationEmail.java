package com.mt.access.domain.model.verification_code;

import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.validate.Validator;

public class RegistrationEmail extends DomainId {
    public RegistrationEmail(String emailRaw) {
        super(emailRaw);
        Validator.isEmail(emailRaw);
    }

    private RegistrationEmail() {
    }
}
