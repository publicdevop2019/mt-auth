package com.mt.common.domain.model.domain_event;

import com.mt.common.domain.model.domain_id.DomainId;

/**
 * domain id used for general purpose, like app started .etc
 */
public class AnyDomainId extends DomainId {

    public static final String DOMAIN_ID = "SYSTEM";

    public AnyDomainId(String domainId) {
        super(domainId);
    }

    public AnyDomainId() {
        super(DOMAIN_ID);
    }

    public static boolean isSystemId(String domainId) {
        return DOMAIN_ID.equals(domainId);
    }
}
