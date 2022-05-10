package com.mt.access.domain.model.permission;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.domain_id.GeneratedDomainId;
import java.io.Serializable;

public class PermissionId extends GeneratedDomainId implements Serializable {
    public PermissionId() {
        super();
    }

    public PermissionId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0Y";
    }
}
