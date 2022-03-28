package com.mt.access.domain.model.organization;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import java.io.Serializable;

public class OrganizationId extends DomainId implements Serializable {
    public OrganizationId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0O" + s.toUpperCase());
    }

    public OrganizationId(String domainId) {
        super(domainId);
    }
}
