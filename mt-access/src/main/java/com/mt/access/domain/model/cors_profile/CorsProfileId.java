package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
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
