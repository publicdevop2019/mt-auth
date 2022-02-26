package com.mt.access.messenger.domain.model.mall_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domainId.DomainId;

public class MallNotificationId extends DomainId {
    public MallNotificationId() {
        super();
        Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        String s = Long.toString(id, 36);
        setDomainId("4M" + s.toUpperCase());
    }

    public MallNotificationId(String domainId) {
        super(domainId);
    }
}
