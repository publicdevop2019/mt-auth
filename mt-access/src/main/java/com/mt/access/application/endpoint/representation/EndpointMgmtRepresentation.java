package com.mt.access.application.endpoint.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.validate.Utility;
import java.util.Optional;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EndpointMgmtRepresentation {
    private String id;
    private String name;
    private String description;
    private String resourceId;
    private String resourceName;
    private String projectId;
    private String path;
    private String method;
    private Boolean websocket;
    private Boolean csrfEnabled;
    private Boolean secured;
    private CorsConfig corsConfig;
    private CacheConfig cacheConfig;
    @JsonIgnore
    private transient CorsProfileId corsProfileId;
    @JsonIgnore
    private transient ClientId clientId;
    @JsonIgnore
    private transient CacheProfileId cacheProfileId;

    public EndpointMgmtRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.name = endpoint.getName();
        this.websocket = endpoint.getWebsocket();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.projectId = endpoint.getProjectId().getDomainId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.secured = endpoint.getSecured();
        this.csrfEnabled = endpoint.getCsrfEnabled();
        this.corsProfileId = endpoint.getCorsProfileId();
        this.cacheProfileId = endpoint.getCacheProfileId();
        this.clientId = endpoint.getClientId();

        Optional<Client> clientFetched =
            DomainRegistry.getClientRepository().query(new ClientQuery(clientId))
                .findFirst();
        if (cacheProfileId != null) {
            CacheProfile cacheFetched =
                DomainRegistry.getCacheProfileRepository()
                    .get(cacheProfileId);
            Set<CacheControlValue> query =
                DomainRegistry.getCacheControlRepository().query(cacheFetched);
            this.cacheConfig = new CacheConfig(cacheFetched, query);
        }
        if (corsProfileId != null) {
            CorsProfile corsProfile = DomainRegistry.getCorsProfileRepository().get(corsProfileId);
            Set<String> allowed =
                DomainRegistry.getCorsAllowedHeaderRepository().query(corsProfile);
            Set<String> exposed =
                DomainRegistry.getCorsExposedHeaderRepository().query(corsProfile);
            Set<Origin> origins = DomainRegistry.getCorsOriginRepository().query(corsProfile);
            this.corsConfig = new CorsConfig(corsProfile, origins, allowed, exposed);
        }
        this.resourceName = clientFetched.get().getName();
        this.path = "/" + clientFetched.get().getPath() + "/" + this.path;
    }

    @Data
    private static class CorsConfig {
        private Set<String> origin;
        private Boolean credentials;
        private Set<String> allowedHeaders;
        private Set<String> exposedHeaders;
        private Long maxAge;

        public CorsConfig(CorsProfile e, Set<Origin> origins, Set<String> allowed,
                          Set<String> exposed) {
            this.origin = Utility.mapToSet(origins, Origin::getValue);
            this.credentials = e.getAllowCredentials();
            this.allowedHeaders = allowed;
            this.exposedHeaders = exposed;
            this.maxAge = e.getMaxAge();
        }
    }

    @Data
    private static class CacheConfig {
        private Boolean allowCache;
        private Set<String> cacheControl;

        private Long expires;

        private Long maxAge;

        private Long smaxAge;

        private String vary;

        private Boolean etag;

        private Boolean weakValidation;

        public CacheConfig(CacheProfile cacheProfile, Set<CacheControlValue> values) {
            allowCache = cacheProfile.getAllowCache();
            cacheControl = Utility.mapToSet(values, e -> e.label);
            expires = cacheProfile.getExpires();
            maxAge = cacheProfile.getMaxAge();
            smaxAge = cacheProfile.getSmaxAge();
            vary = cacheProfile.getVary();
            etag = cacheProfile.getEtag();
            weakValidation = cacheProfile.getWeakValidation();
        }
    }
}
