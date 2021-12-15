package com.mt.access.domain.model.endpoint.event;

import com.mt.common.domain.model.domain_event.DomainEvent;

public class EndpointReloadRequested extends DomainEvent {

    public static final String ENDPOINT_RELOAD_REQUESTED = "endpoint_reload_requested";
    public static final String name = "ENDPOINT_RELOAD_REQUESTED";
    public EndpointReloadRequested() {
        super();
        setInternal(false);
        setTopic(ENDPOINT_RELOAD_REQUESTED);
        setName(name);
    }
}
