package com.mt.access.application.endpoint.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.RouterId;
import com.mt.access.domain.model.project.ProjectId;
import lombok.Data;

@Data
public class EndpointSharedCardRepresentation {
    private String id;
    private String name;
    private String description;
    private String path;
    private String method;
    private String routerId;
    private Integer version;
    private Boolean websocket;
    private Boolean shared;
    private Boolean secured;
    private String projectId;
    private String projectName;
    @JsonIgnore
    private transient RouterId rawRouterId;
    @JsonIgnore
    private transient ProjectId originalProjectId;

    public EndpointSharedCardRepresentation(Endpoint endpoint) {
        this.rawRouterId = endpoint.getRouterId();
        this.routerId = endpoint.getRouterId().getDomainId();
        this.projectId = endpoint.getProjectId().getDomainId();
        this.originalProjectId = endpoint.getProjectId();
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.name = endpoint.getName();
        this.websocket = endpoint.getWebsocket();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.version = endpoint.getVersion();
        this.shared = endpoint.getShared();
        this.secured = endpoint.getSecured();
    }
}
