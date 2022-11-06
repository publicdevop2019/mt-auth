package com.mt.common.domain.model.domain_event.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;

public class ApplicationStartedEvent {
    private ApplicationStartedEvent() {

    }

    public static StoredEvent create() {
        DomainEvent domainEvent = new DomainEvent();
        domainEvent.setName("ApplicationStarted");
        domainEvent.setDomainId(new AnyDomainId("SYSTEM"));
        return new StoredEvent(domainEvent);
    }
}
