package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class PwdResetCode implements Serializable {
    public static final Integer EXPIRE_AFTER_MILLI = 5 * 60 * 1000;
    public static final String OPERATION_TYPE = "PWD_RESET";
    @Getter
    private String value;

    public PwdResetCode() {
        setValue(DomainRegistry.getPwdResetTokenGeneratorService().generate());
    }

    public PwdResetCode(String value) {
        setValue(value);
    }

    private void setValue(String value) {
        Validator.notNull(value);
        Validator.greaterThanOrEqualTo(value, 9);
        this.value = value;
    }
}
