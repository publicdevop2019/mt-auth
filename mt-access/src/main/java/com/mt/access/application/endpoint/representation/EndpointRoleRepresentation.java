package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointRoleRepresentation {
    private String id;
    private String name;

    public EndpointRoleRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.name = endpoint.getName();
    }
}
