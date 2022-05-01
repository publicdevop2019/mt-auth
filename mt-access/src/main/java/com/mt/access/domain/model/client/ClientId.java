package com.mt.access.domain.model.client;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.domain_id.GeneratedDomainId;
import java.io.Serializable;

public class ClientId extends GeneratedDomainId implements Serializable {
    public ClientId() {
        super();
    }

    public ClientId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0C";
    }
}
