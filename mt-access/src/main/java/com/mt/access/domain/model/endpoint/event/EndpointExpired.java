package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EndpointExpired extends DomainEvent {
    public static final String ENDPOINT_EXPIRED = "endpoint_expired";
    public static final String name = "ENDPOINT_EXPIRED";

    {
        setTopic(ENDPOINT_EXPIRED);
        setName(name);
    }

    public EndpointExpired(Endpoint endpoint) {
        super(endpoint.getEndpointId());
    }
}
