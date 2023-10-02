package com.mt.access.domain.model.activation_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import lombok.Getter;

/**
 * user activation code.
 */
public class ActivationCode {
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
        setActivationCode(activationCode);
    }

    public void setActivationCode(String activationCode) {
        Validator.notNull(activationCode);
        Validator.notBlank(activationCode);
        Validator.lessThanOrEqualTo(activationCode, 6);
        Validator.greaterThanOrEqualTo(activationCode, 6);
        this.activationCode = activationCode;
    }
}
