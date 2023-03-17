package com.mt.access.domain.model.pending_user;

import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Embeddable;

@Embeddable
public class RegistrationEmail extends DomainId {
    public RegistrationEmail(String emailRaw) {
        super(emailRaw);
        Validator.isEmail(emailRaw);
    }

    private RegistrationEmail() {
    }
}
