package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

import java.io.Serializable;

public class CacheProfileId extends DomainId implements Serializable {
    public CacheProfileId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0X" + s.toUpperCase());
    }

    public CacheProfileId(String domainId) {
        super(domainId);
    }
}
