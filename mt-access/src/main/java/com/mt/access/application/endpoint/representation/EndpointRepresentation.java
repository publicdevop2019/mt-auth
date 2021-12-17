package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

import java.util.Set;

@Data
public class EndpointRepresentation {
    private String id;
    private String resourceId;
    private String description;
    private String path;
    private String method;
    private String createdBy;
    private Long createdAt;
    private String modifiedBy;
    private Long modifiedAt;
    private Set<String> clientRoles;
    private Set<String> userRoles;
    private boolean secured;
    private boolean userOnly;
    private boolean clientOnly;
    private boolean websocket;
    private boolean csrfEnabled;
    private String corsProfileId;
    private String roleId;
    private Integer version;
    private String cacheProfileId;
    public EndpointRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.roleId = endpoint.getSystemRoleId() != null ? endpoint.getSystemRoleId().getDomainId() : null;
        this.websocket = endpoint.isWebsocket();
        this.secured = endpoint.isSecured();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.description = endpoint.getDescription();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.createdBy = endpoint.getCreatedBy();
        this.createdAt = endpoint.getCreatedAt() != null ? endpoint.getCreatedAt().getTime() : null;
        this.modifiedBy = endpoint.getModifiedBy();
        this.modifiedAt = endpoint.getModifiedAt() != null ? endpoint.getModifiedAt().getTime() : null;
        this.version = endpoint.getVersion();
        this.csrfEnabled = endpoint.isCsrfEnabled();
        this.corsProfileId = endpoint.getCorsProfileId() != null ? endpoint.getCorsProfileId().getDomainId() : null;
        this.cacheProfileId = endpoint.getCacheProfileId()!=null?endpoint.getCacheProfileId().getDomainId():null;;
    }
}
