package com.mt.common.domain.model.domain_event.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;

public class ApplicationStartedEvent {

    public static final String APPLICATION_STARTED = "APPLICATION_STARTED";

    private ApplicationStartedEvent() {

    }

    public static StoredEvent create() {
        DomainEvent domainEvent = new DomainEvent(new AnyDomainId());
        domainEvent.setName(APPLICATION_STARTED);
        return new StoredEvent(domainEvent);
    }
}
