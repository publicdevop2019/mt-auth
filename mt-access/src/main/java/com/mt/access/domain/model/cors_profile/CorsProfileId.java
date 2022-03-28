package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;

public class CorsProfileId extends DomainId implements Serializable {
    public CorsProfileId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0O" + s.toUpperCase());
    }

    public CorsProfileId(String domainId) {
        super(domainId);
    }
}
