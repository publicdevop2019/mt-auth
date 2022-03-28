package com.mt.access.domain.model.notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_id.DomainId;

public class NotificationId extends DomainId {
    public NotificationId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("4S" + s.toUpperCase());
    }

    public NotificationId(String domainId) {
        super(domainId);
    }
}
