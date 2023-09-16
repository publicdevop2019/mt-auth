package com.mt.access.application.endpoint.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.endpoint.Endpoint;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EndpointMgmtRepresentation {
    private String id;
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
            Optional<CacheProfile> cacheFetched =
                DomainRegistry.getCacheProfileRepository()
                    .query(CacheProfileQuery.internalQuery(cacheProfileId))
                    .findFirst();
            this.cacheConfig = new CacheConfig(cacheFetched.get());
        }
        if (corsProfileId != null) {
            Optional<CorsProfile> corsFetched =
                DomainRegistry.getCorsProfileRepository().query(new CorsProfileQuery(corsProfileId))
                    .findFirst();
            this.corsConfig = new CorsConfig(corsFetched.get());
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

        public CorsConfig(CorsProfile e) {
            this.origin =
                e.getAllowOrigin().stream().map(Origin::getValue).sorted().collect(
                    Collectors.toCollection(LinkedHashSet::new));
            this.credentials = e.getAllowCredentials();
            this.allowedHeaders = e.getAllowedHeaders().stream().sorted().collect(
                Collectors.toCollection(LinkedHashSet::new));
            this.exposedHeaders = e.getExposedHeaders().stream().sorted().collect(
                Collectors.toCollection(LinkedHashSet::new));
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

        public CacheConfig(CacheProfile cacheProfile) {
            allowCache = cacheProfile.getAllowCache();
            cacheControl = cacheProfile.getCacheControl().stream().map(e -> e.label)
                .sorted().collect(Collectors.toCollection(LinkedHashSet::new));
            expires = cacheProfile.getExpires();
            maxAge = cacheProfile.getMaxAge();
            smaxAge = cacheProfile.getSmaxAge();
            vary = cacheProfile.getVary();
            etag = cacheProfile.getEtag();
            weakValidation = cacheProfile.getWeakValidation();
        }
    }
}
