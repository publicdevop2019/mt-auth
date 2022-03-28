package com.mt.access.domain.model.permission;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;
import java.io.Serializable;

public class PermissionId extends DomainId implements Serializable {
    public PermissionId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0Y" + s.toUpperCase());
    }

    public PermissionId(String domainId) {
        super(domainId);
    }
}
