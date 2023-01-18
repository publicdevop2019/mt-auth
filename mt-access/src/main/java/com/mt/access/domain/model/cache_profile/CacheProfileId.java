package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class CacheProfileId extends GeneratedDomainId implements Serializable {
    public CacheProfileId() {
        super();
    }

    public CacheProfileId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0X";
    }
}
