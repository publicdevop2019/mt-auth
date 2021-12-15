package com.mt.common.domain.model.domainId;

import com.google.common.base.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@NoArgsConstructor
@MappedSuperclass
public class DomainId implements Serializable {
    @Getter
    @Column(unique = true, updatable = false, nullable = false)
    @Setter(AccessLevel.PROTECTED)
    private String domainId;

    public DomainId(String domainId) {
        if (domainId == null)
            throw new IllegalStateException("null domain id is not allowed");
        if (this.domainId != null)
            throw new IllegalStateException("domain id already present");
        this.domainId = domainId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainId)) return false;
        DomainId domainId1 = (DomainId) o;
        return Objects.equal(domainId, domainId1.domainId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(domainId);
    }
}
