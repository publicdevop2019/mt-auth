package com.mt.access.domain.model.activation_code;

import com.mt.common.domain.model.validate.Validator;
import com.mt.access.domain.DomainRegistry;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

public class ActivationCode {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private String activationCode;

    public ActivationCode() {
        setActivationCode(DomainRegistry.getActivationCodeService().generate());
    }

    public ActivationCode(String activationCode) {
        Validator.lengthGreaterThanOrEqualTo(activationCode, 6);
        if (!StringUtils.hasText(activationCode))
            throw new IllegalArgumentException("activationCode is empty");
        setActivationCode(activationCode);
    }
}
