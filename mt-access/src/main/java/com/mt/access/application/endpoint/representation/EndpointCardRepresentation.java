package com.mt.access.application.endpoint.representation;

import com.mt.access.domain.model.endpoint.Endpoint;
import lombok.Data;

@Data
public class EndpointCardRepresentation {
    private String id;
    private String name;
    private String description;
    private String path;
    private String method;
    private String routerId;
    private String routerName;
    private Integer version;
    private Boolean websocket;
    private Boolean csrfEnabled;
    private Boolean secured;
    private Boolean shared;
    private Boolean expired;
    private Boolean external;
    private String expireReason;
    private String corsProfileId;
    private String cacheProfileId;

    public EndpointCardRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.external = endpoint.getExternal();
        this.name = endpoint.getName();
        this.cacheProfileId =
            endpoint.getCacheProfileId() != null ? endpoint.getCacheProfileId().getDomainId() :
                null;
        this.routerId = endpoint.getRouterId().getDomainId();
        this.websocket = endpoint.getWebsocket();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.version = endpoint.getVersion();
        this.secured = endpoint.getSecured();
        this.csrfEnabled = endpoint.getCsrfEnabled();
        this.shared = endpoint.getShared();
        this.expired = endpoint.getExpired();
        this.expireReason = endpoint.getExpireReason();
        this.corsProfileId =
            endpoint.getCorsProfileId() != null ? endpoint.getCorsProfileId().getDomainId() : null;
    }
}
