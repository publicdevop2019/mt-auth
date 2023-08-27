package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data
@Embeddable
@EqualsAndHashCode
public class MfaCode implements Serializable {
    @Getter
    @Column(name = "mfa_code")
    private String value;

    public MfaCode() {
        setValue(DomainRegistry.getMfaCodeService().generate());
    }

    public MfaCode(String value) {
        this.value = value;
    }

    private void setValue(String value) {
        this.value = value;
    }

}