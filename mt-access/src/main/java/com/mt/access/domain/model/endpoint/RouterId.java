package com.mt.access.domain.model.endpoint;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class RouterId  extends GeneratedDomainId implements Serializable {
    public RouterId() {
        super();
    }

    public RouterId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0R";
    }
}
