package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class EndpointSharedCardRepresentation {
    private String id;
    private String name;
    private String description;
    private String path;
    private String method;
    private Integer version;
    private Boolean websocket;
    private Boolean shared;
    private Boolean secured;
    private String projectId;
    private String projectName;
    private transient ProjectId originalProjectId;
    private transient ClientId clientId;

    public EndpointSharedCardRepresentation(Endpoint endpoint) {
        this.clientId = endpoint.getClientId();
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
