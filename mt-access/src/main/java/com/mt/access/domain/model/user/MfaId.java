package com.mt.access.domain.model.user;

import com.google.common.base.Objects;
import java.util.UUID;
import javax.persistence.Column;
import lombok.Getter;

public class MfaId {
    @Getter
    @Column(name = "mfa_id")
    private final String value;

    public MfaId() {
        value = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MfaId)) {
            return false;
        }
        MfaId that = (MfaId) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
