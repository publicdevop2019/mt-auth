package com.mt.access.domain.model.organization;

import com.mt.common.domain.model.domain_id.GeneratedDomainId;
import java.io.Serializable;

public class OrganizationId extends GeneratedDomainId implements Serializable {
    public OrganizationId() {
        super();
    }

    public OrganizationId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0G";
    }
}
