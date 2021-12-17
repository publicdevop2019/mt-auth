package com.mt.access.domain.model.system_role;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import java.io.Serializable;

public class SystemRoleId  extends DomainId implements Serializable {

    public SystemRoleId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0R" + s.toUpperCase());
    }

    public SystemRoleId(String domainId) {
        super(domainId);
    }

}
