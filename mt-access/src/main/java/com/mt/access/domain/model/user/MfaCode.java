package com.mt.access.domain.model.user;

import com.google.common.base.Objects;
import com.mt.access.domain.DomainRegistry;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Embeddable
public class MfaCode implements Serializable {
    @Getter
    @Setter
    @Column(name = "mfa_code")
    private String value;

    public MfaCode() {
        setValue(DomainRegistry.getMfaCodeService().generate());
    }

    private void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MfaCode)) {
            return false;
        }
        MfaCode that = (MfaCode) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}