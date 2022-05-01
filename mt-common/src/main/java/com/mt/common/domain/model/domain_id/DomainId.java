package com.mt.common.domain.model.domain_id;

import com.google.common.base.Objects;
import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;

@MappedSuperclass
public class DomainIdNew {
    @Getter
    @Column(unique = true, updatable = false, nullable = false)
    private String domainId;

    public DomainIdNew() {
        String prefix = getPrefix();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        this.domainId = prefix + s.toUpperCase();
    }

    public DomainIdNew(String domainId) {
        if (domainId == null) {
            throw new IllegalStateException("null domain id is not allowed");
        }
        if (domainId.isBlank() || domainId.isEmpty()) {
            throw new IllegalStateException("empty or blank domain id is not allowed");
        }
        if (this.domainId != null) {
            throw new IllegalStateException("domain id already present");
        }
        String prefix = getPrefix();
        if (domainId.indexOf(prefix) != 0) {
            throw new IllegalStateException("domain id wrong prefix");
        }
        this.domainId = domainId;
    }

    public String getPrefix() {
        throw new IllegalArgumentException("please override getPrefix method");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomainIdNew)) {
            return false;
        }
        DomainIdNew domainId1 = (DomainIdNew) o;
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
