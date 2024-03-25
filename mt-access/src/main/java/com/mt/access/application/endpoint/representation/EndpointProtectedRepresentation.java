package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointProtectedRepresentation {
    private String id;
    private String permissionId;
    private String name;

    public EndpointProtectedRepresentation(Endpoint endpoint) {
        this.name = endpoint.getName();
        this.id = endpoint.getEndpointId().getDomainId();
        this.permissionId = endpoint.getPermissionId().getDomainId();
    }
}
