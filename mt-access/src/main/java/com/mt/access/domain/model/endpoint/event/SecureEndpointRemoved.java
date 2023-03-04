package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SecureEndpointRemoved extends DomainEvent {
    public static final String SECURE_ENDPOINT_REMOVED = "secure_endpoint_removed";
    public static final String name = "SECURE_ENDPOINT_REMOVED";
    private Set<PermissionId> permissionIds;

    {
        setTopic(SECURE_ENDPOINT_REMOVED);
        setName(name);

    }

    public SecureEndpointRemoved(Set<Endpoint> endpoint) {
        super();
        setDomainIds(endpoint.stream().map(Endpoint::getEndpointId).collect(Collectors.toSet()));
        this.permissionIds =
            endpoint.stream().map(Endpoint::getPermissionId).collect(Collectors.toSet());
    }
}
