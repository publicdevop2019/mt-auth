package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SecureEndpointRemoved extends DomainEvent {
    public static final String SECURE_ENDPOINT_REMOVED = "secure_endpoint_removed";
    public static final String name = "SECURE_ENDPOINT_REMOVED";
    private PermissionId permissionId;
    private String changeId;

    {
        setTopic(SECURE_ENDPOINT_REMOVED);
        setName(name);

    }

    public SecureEndpointRemoved(Endpoint endpoint) {
        super(endpoint.getEndpointId());
        this.permissionId = endpoint.getPermissionId();
        this.changeId = endpoint.getPermissionId().getDomainId() + "_cancel";
    }
}
