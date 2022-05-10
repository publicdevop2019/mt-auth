package com.mt.common.domain.model.domain_id;

import com.google.common.base.Objects;
import com.mt.common.domain.CommonDomainRegistry;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DDD domain id
 * can be any form e.g. test@test.com or 0CABCEDFGHI
 * it serve as an identifier in a domain
 */
@MappedSuperclass
public class DomainId implements Serializable {
    @Getter
    @Column(unique = true, updatable = false, nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private String domainId;

    protected DomainId() {
    }

    protected DomainId(String domainId) {
        if (domainId == null) {
            throw new IllegalStateException("null domain id is not allowed");
        }
        if (domainId.isBlank() || domainId.isEmpty()) {
            throw new IllegalStateException("empty or blank domain id is not allowed");
        }
        this.domainId = domainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomainId)) {
            return false;
        }
        DomainId domainId1 = (DomainId) o;
        return Objects.equal(domainId, domainId1.domainId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
