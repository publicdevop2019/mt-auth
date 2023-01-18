package com.mt.access.domain.model.notification;

import com.mt.common.domain.model.domain_event.GeneratedDomainId;
import java.io.Serializable;

public class NotificationId extends GeneratedDomainId implements Serializable {
    public NotificationId() {
        super();
    }

    public NotificationId(String domainId) {
        super(domainId);
    }

    @Override
    protected String getPrefix() {
        return "4S";
    }
}
