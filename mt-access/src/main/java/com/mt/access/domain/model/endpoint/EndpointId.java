package com.mt.access.domain.model.endpoint;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class EndpointId extends GeneratedDomainId implements Serializable {
    public EndpointId() {
        super();
    }

    public EndpointId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0E";
    }
}
