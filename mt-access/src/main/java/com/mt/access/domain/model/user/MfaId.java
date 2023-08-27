package com.mt.access.domain.model.user;

import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Embeddable
@EqualsAndHashCode
public class MfaId {
    @Getter
    @Column(name = "mfa_id")
    private final String value;

    public MfaId() {
        value = UUID.randomUUID().toString();
    }
    public MfaId(String value) {
        this.value = value;
    }
}
