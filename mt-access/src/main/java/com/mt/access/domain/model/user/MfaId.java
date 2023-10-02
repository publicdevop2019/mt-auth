package com.mt.access.domain.model.user;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class MfaId  implements Serializable {
    @Getter
    private final String value;

    public MfaId() {
        value = UUID.randomUUID().toString();
    }
    public MfaId(String value) {
        this.value = value;
    }
}
