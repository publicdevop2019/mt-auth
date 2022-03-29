package com.mt.access.domain.model.project;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;

public class ProjectId extends DomainId implements Serializable {
    public ProjectId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0P" + s.toUpperCase());
    }

    public ProjectId(String domainId) {
        super(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
