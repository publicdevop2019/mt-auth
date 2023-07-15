package com.mt.common.domain.model.domain_event.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.domain_event.StoredEvent;

public class ApplicationStartedEvent extends DomainEvent {
    public static final String STARTED_ACCESS =
        "started_access";
    public static final String name = "APPLICATION_STARTED";

    public ApplicationStartedEvent() {
        setName(name);
        setTopic(STARTED_ACCESS);
        setInternal(false);
    }
}
