package com.mt.access.application.endpoint.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class EndpointCardRepresentation {
    private String id;
    private String name;
    private String description;
    private String resourceId;
    private String resourceName;
    private String path;
    private String method;
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
        this.websocket = endpoint.getWebsocket();
        this.resourceId = endpoint.getClientId().getDomainId();
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

    public static void updateDetail(List<EndpointCardRepresentation> data) {
        Set<ClientId> collect =
            data.stream().map(e -> new ClientId(e.resourceId)).collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            Set<Client> allByIds =
                ApplicationServiceRegistry.getClientApplicationService().internalQuery(collect);
            data.forEach(e -> allByIds.stream()
                .filter(ee -> ee.getClientId().getDomainId().equals(e.resourceId)).findFirst()
                .ifPresent(ee -> {
                    e.resourceName = ee.getName();
                }));
        }
    }
}
