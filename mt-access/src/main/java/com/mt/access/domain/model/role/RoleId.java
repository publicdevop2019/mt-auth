package com.mt.access.domain.model.role;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;

public class RoleId extends DomainId implements Serializable {
    public RoleId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0Z" + s.toUpperCase());
    }

    public RoleId(String domainId) {
        super(domainId);
    }
}
