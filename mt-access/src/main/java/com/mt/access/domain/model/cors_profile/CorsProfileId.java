package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.domain_id.GeneratedDomainId;
import java.io.Serializable;

public class CorsProfileId extends GeneratedDomainId implements Serializable {
    public CorsProfileId() {
        super();
    }

    public CorsProfileId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0O";
    }
}
