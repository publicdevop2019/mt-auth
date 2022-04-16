package com.mt.access.domain.model.client;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import java.io.Serializable;

public class ClientId extends DomainId implements Serializable {

    public ClientId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("0C" + s.toUpperCase());
    }

    public ClientId(String domainId) {
        super(domainId);
    }

}
