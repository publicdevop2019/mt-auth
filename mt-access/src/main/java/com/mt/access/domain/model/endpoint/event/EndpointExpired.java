package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class EndpointExpired extends DomainEvent {
    public static final String ENDPOINT_EXPIRED = "endpoint_expired";
    public static final String name = "ENDPOINT_EXPIRED";

    public EndpointExpired(Endpoint endpoint) {
        super();
        setTopic(ENDPOINT_EXPIRED);
        setName(name);
        setDomainId(endpoint.getEndpointId());
    }
}
