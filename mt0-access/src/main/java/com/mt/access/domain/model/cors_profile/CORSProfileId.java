package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import java.io.Serializable;

public class CORSProfileId extends DomainId implements Serializable {
    public CORSProfileId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0O" + s.toUpperCase());
    }

    public CORSProfileId(String domainId) {
        super(domainId);
    }
}
