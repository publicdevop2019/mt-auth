package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@EqualsAndHashCode
public class PasswordResetCode {
    @Getter
    private String value;

    public PasswordResetCode() {
        setValue(DomainRegistry.getPasswordResetTokenService().generate());
    }

    public PasswordResetCode(String value) {
        setValue(value);
    }

    private void setValue(String value) {
        Validator.notNull(value);
        Validator.greaterThanOrEqualTo(value, 9);
        this.value = value;
    }

}
