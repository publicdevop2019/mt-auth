package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.RouterId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RouterKeyParamChanged extends DomainEvent {

    public static final String ROUTER_CHANGED = "router_key_param_changed";
    public static final String name = "ROUTER_KEY_PARAM_CHANGED";

    {
        setTopic(ROUTER_CHANGED);
        setName(name);
    }

    public RouterKeyParamChanged(RouterId routerId) {
        super(routerId);
    }
}