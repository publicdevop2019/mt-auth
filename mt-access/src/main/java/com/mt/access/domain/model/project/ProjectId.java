package com.mt.access.domain.model.project;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class ProjectId extends GeneratedDomainId implements Serializable {
    public ProjectId() {
        super();
    }

    public ProjectId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return getIdPrefix();
    }

    public static String getIdPrefix() {
        return "0P";
    }
}