package com.mt.common.domain.model.job;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import java.io.Serializable;

public class JobId extends DomainId implements Serializable {
    public JobId() {
        super();
        long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0J" + s.toUpperCase());
    }

    public JobId(String domainId) {
        super(domainId);
    }

    @Override
    public String toString() {
        return getDomainId();
    }
}
