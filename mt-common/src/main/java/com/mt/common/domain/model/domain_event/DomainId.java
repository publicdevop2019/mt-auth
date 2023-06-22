package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * DDD domain id
 * can be any form e.g. test@test.com or 0CABCEDFGHI
 * it serve as an identifier in a domain
 */
@MappedSuperclass
@EqualsAndHashCode
public class DomainId implements Serializable {
    @Getter
    @Column(unique = true, updatable = false, nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private String domainId;

    protected DomainId() {
    }

    protected DomainId(String domainId) {
        if (domainId == null) {
            throw new DefinedRuntimeException("null domain id is not allowed", "0010",
                HttpResponseCode.BAD_REQUEST);
        }
        if (domainId.isBlank() || domainId.isEmpty()) {
            throw new DefinedRuntimeException("empty or blank domain id is not allowed", "0011",
                HttpResponseCode.BAD_REQUEST);
        }
        this.domainId = domainId;
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
