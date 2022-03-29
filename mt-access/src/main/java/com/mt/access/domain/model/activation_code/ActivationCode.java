package com.mt.access.domain.model.activation_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * user activation code.
 */
public class ActivationCode {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private String activationCode;

    public ActivationCode() {
        setActivationCode(DomainRegistry.getActivationCodeService().generate());
    }

    /**
     * create activation code.
     *
     * @param activationCode string value of activation code
     */
    public ActivationCode(String activationCode) {
        Validator.lengthGreaterThanOrEqualTo(activationCode, 6);
        if (!StringUtils.hasText(activationCode)) {
            throw new IllegalArgumentException("activationCode is empty");
        }
        setActivationCode(activationCode);
    }
}
