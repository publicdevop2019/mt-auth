package com.mt.access.domain.model.client;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class ClientId extends GeneratedDomainId implements Serializable {
    public ClientId() {
        super();
    }

    public ClientId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0C";
    }

    public static String getClientPrefix() {
        return "0C";
    }

}
