package com.mt.access.domain.model.position;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;

public class PositionId extends DomainId implements Serializable {
    public PositionId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0L" + s.toUpperCase());
    }

    public PositionId(String domainId) {
        super(domainId);
    }
}
