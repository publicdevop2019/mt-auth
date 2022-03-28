package com.mt.access.domain.model.pending_user;

import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RegistrationEmail extends DomainId {
    @Getter
    private String email;

    public RegistrationEmail(String email) {
        super(email);
        setEmail(email);
    }

    private void setEmail(String email) {
        Validator.notNull(email);
        Validator.notBlank(email);
        Validator.isEmail(email);
        this.email = email;
    }

    @Override
    public String getDomainId() {
        return this.email;
    }
}
