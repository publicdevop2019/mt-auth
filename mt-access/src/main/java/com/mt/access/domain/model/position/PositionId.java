package com.mt.access.domain.model.position;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class PositionId extends GeneratedDomainId implements Serializable {
    public PositionId() {
        super();
    }

    public PositionId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "0L";
    }
}
