package com.mt.access.domain.model.user;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class UserId extends GeneratedDomainId implements Serializable {
    public UserId() {
        super();
    }

    public UserId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0U";
    }
}