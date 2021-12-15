package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointCardRepresentation {
    private String id;
    private String description;
    private String resourceId;
    private String path;
    private String method;
    private Integer version;
    private boolean websocket;
    private boolean csrfEnabled;
    private boolean secured;
    private String corsProfileId;
    private String roleId;
    private String cacheProfileId;
    public EndpointCardRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.cacheProfileId = endpoint.getCacheProfileId()!=null?endpoint.getCacheProfileId().getDomainId():null;;
        this.websocket = endpoint.isWebsocket();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.version = endpoint.getVersion();
        this.roleId = endpoint.getSystemRoleId()!=null?endpoint.getSystemRoleId().getDomainId():null;
        this.secured = endpoint.isSecured();
        this.csrfEnabled = endpoint.isCsrfEnabled();
        this.corsProfileId = endpoint.getCorsProfileId()!=null?endpoint.getCorsProfileId().getDomainId():null;
    }
}
