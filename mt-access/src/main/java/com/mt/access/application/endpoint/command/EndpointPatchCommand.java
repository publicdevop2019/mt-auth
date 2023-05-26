package com.mt.access.application.endpoint.command;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointPatchCommand {
    private String description;
    private String name;
    private String path;
    private String method;

    public EndpointPatchCommand(Endpoint endpoint) {
        this.description = endpoint.getDescription();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.name = endpoint.getName();
    }
}
