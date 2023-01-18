package com.mt.access.domain.model.sub_request;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class SubRequestId extends GeneratedDomainId implements Serializable {
    public SubRequestId() {
        super();
    }

    public SubRequestId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0S";
    }
}