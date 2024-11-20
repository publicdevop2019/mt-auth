package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@EqualsAndHashCode
public class MfaCode implements Serializable {
    public static final Integer EXPIRE_AFTER_MILLI = 5 * 60 * 1000;
    public static final String OPERATION_TYPE = "LOGIN_MFA";
    @Getter
    private String value;

    public MfaCode() {
        setValue(DomainRegistry.getMfaCodeGeneratorService().generate());
    }

    public MfaCode(String value) {
        this.value = value;
    }

    private void setValue(String value) {
        this.value = value;
    }

}