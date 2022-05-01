package com.mt.access.domain.model.user;

import com.mt.common.domain.model.domain_id.GeneratedDomainId;
import java.io.Serializable;
import javax.persistence.Embeddable;

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