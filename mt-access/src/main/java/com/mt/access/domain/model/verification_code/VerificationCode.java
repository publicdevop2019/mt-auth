package com.mt.access.domain.model.verification_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class VerificationCode {
    public static final Integer EXPIRE_AFTER_MILLI = 5 * 60 * 1000;
    public static final String OPERATION_TYPE = "LOGIN";
    @Getter
    private String value;

    public VerificationCode() {
        setValue(DomainRegistry.getLoginCodeGenerator().generate());
    }

    public VerificationCode(String value) {
        setValue(value);
    }

    public void setValue(String value) {
        Validator.notNull(value);
        Validator.notBlank(value);
        Validator.lessThanOrEqualTo(value, 6);
        Validator.greaterThanOrEqualTo(value, 6);
        this.value = value;
    }


}
