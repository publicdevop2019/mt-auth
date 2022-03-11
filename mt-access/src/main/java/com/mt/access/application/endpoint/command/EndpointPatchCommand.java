package com.mt.access.application.endpoint.command;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.restful.TypedClass;
import lombok.Data;

@Data
public class EndpointPatchCommand extends TypedClass<EndpointPatchCommand> {
    private String description;
    private String name;
    private String resourceId;
    private String path;
    private String method;

    public EndpointPatchCommand(Endpoint bizEndpoint) {
        super(EndpointPatchCommand.class);
        this.description = bizEndpoint.getDescription();
        this.resourceId = bizEndpoint.getClientId().getDomainId();
        this.path = bizEndpoint.getPath();
        this.method = bizEndpoint.getMethod();
        this.name = bizEndpoint.getName();
    }

    public EndpointPatchCommand() {
        super(EndpointPatchCommand.class);
    }

}
