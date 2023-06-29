package com.mt.access.domain.model.endpoint.event;

import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class EndpointReloadRequested extends DomainEvent {

    public static final String ENDPOINT_RELOAD_REQUESTED = "endpoint_reload_requested";
    public static final String name = "ENDPOINT_RELOAD_REQUESTED";

    {
        setTopic(ENDPOINT_RELOAD_REQUESTED);
        setName(name);

    }

    public EndpointReloadRequested() {
        super(new AnyDomainId());
        setInternal(false);
    }
}
