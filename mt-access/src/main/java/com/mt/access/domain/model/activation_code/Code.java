package com.mt.access.domain.model.activation_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Code {
    @Getter
    private String value;

    public Code() {
        setValue(DomainRegistry.getActivationCodeService().generate());
    }

    public Code(String value) {
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
