package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EndpointShareAdded extends DomainEvent {
    public static final String ENDPOINT_SHARED_ADDED = "endpoint_shared_added";
    public static final String name = "ENDPOINT_SHARED_ADDED";
    private PermissionId permissionId;

    public EndpointShareAdded(Endpoint endpoint) {
        super(endpoint.getEndpointId());
        setTopic(ENDPOINT_SHARED_ADDED);
        setName(name);
        this.permissionId = endpoint.getPermissionId();
    }
}
