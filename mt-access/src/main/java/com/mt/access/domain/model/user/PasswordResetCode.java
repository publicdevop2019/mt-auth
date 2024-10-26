package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PasswordResetCode implements Serializable {
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
