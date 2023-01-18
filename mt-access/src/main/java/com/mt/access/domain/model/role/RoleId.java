package com.mt.access.domain.model.role;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class RoleId extends GeneratedDomainId implements Serializable {
    public RoleId() {
        super();
    }

    public RoleId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0Z";
    }
}