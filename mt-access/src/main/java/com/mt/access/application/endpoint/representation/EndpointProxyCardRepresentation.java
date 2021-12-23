package com.mt.access.application.endpoint.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.cors_profile.CORSProfile;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.access.domain.model.cors_profile.CORSProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class EndpointProxyCardRepresentation implements Serializable, Comparable<EndpointProxyCardRepresentation> {
    private String id;
    private String description;
    private String resourceId;
    private String path;
    private String method;
    private boolean websocket;
    private boolean csrfEnabled;
    private boolean secured;
    private CorsConfig corsConfig;
    private CacheConfig cacheConfig;
    @JsonIgnore
    private transient CORSProfileId corsProfileId;
    @JsonIgnore
    private transient ClientId clientId;
    @JsonIgnore
    private transient CacheProfileId cacheProfileId;
    private String roleId;

    public EndpointProxyCardRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.websocket = endpoint.isWebsocket();
        this.resourceId = endpoint.getClientId().getDomainId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.secured = endpoint.isSecured();
        this.csrfEnabled = endpoint.isCsrfEnabled();
        this.corsProfileId = endpoint.getCorsProfileId();
        this.cacheProfileId = endpoint.getCacheProfileId();
        this.clientId = endpoint.getClientId();
        this.roleId = endpoint.getSystemRoleId() == null ? null : endpoint.getSystemRoleId().getDomainId();
    }

    public static void updateDetail(List<EndpointProxyCardRepresentation> original) {
        Set<ClientId> clients = original.stream().map(EndpointProxyCardRepresentation::getClientId).collect(Collectors.toSet());
        Set<CacheProfileId> cache = original.stream().map(EndpointProxyCardRepresentation::getCacheProfileId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<CORSProfileId> cors = original.stream().map(EndpointProxyCardRepresentation::getCorsProfileId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<CORSProfile> corsFetched = null;
        Set<CacheProfile> cacheFetched = null;
        Set<Client> clientFetched = null;
        if (cors.size() > 0) {
            corsFetched = QueryUtility.getAllByQuery((query) -> DomainRegistry.getCorsProfileRepository().corsProfileOfQuery((CORSProfileQuery) query), new CORSProfileQuery(cors));
        }
        if (cache.size() > 0) {
            cacheFetched = QueryUtility.getAllByQuery((query) -> DomainRegistry.getCacheProfileRepository().cacheProfileOfQuery((CacheProfileQuery) query), new CacheProfileQuery(cache));
        }
        if (clients.size() > 0) {
            clientFetched = QueryUtility.getAllByQuery((query) -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) query), new ClientQuery(clients));
        }
        Set<CacheProfile> finalCacheFetched = cacheFetched;
        Set<CORSProfile> finalCorsFetched = corsFetched;
        Set<Client> finalClientFetched = clientFetched;
        original.forEach(rep -> {
            if (finalCacheFetched != null) {
                finalCacheFetched.stream().filter(e -> e.getCacheProfileId().equals(rep.cacheProfileId)).findFirst().ifPresent(e -> rep.cacheConfig = new CacheConfig(e));
            }
            if (finalCorsFetched != null) {
                finalCorsFetched.stream().filter(e -> e.getCorsId().equals(rep.corsProfileId)).findFirst().ifPresent(e -> rep.corsConfig = new CorsConfig(e));
            }
            if (finalClientFetched != null) {
                finalClientFetched.stream().filter(e -> e.getClientId().equals(rep.clientId)).findFirst().ifPresent(e -> {
                    rep.setPath("/" + e.getPath() + "/" + rep.getPath());
                });
            }
        });
    }

    @Override
    public int compareTo(EndpointProxyCardRepresentation o) {
        return this.getId().compareTo(o.getId());
    }

    @Data
    private static class CorsConfig implements Serializable {
        private Set<String> origin;
        private Boolean credentials;
        private Set<String> allowedHeaders;
        private Set<String> exposedHeaders;
        private Long maxAge;

        public CorsConfig(CORSProfile e) {
            this.origin = e.getAllowOrigin().stream().map(Origin::getValue).collect(Collectors.toSet());
            this.credentials = e.isAllowCredentials();
            this.allowedHeaders = e.getAllowedHeaders();
            this.exposedHeaders = e.getExposedHeaders();
            this.maxAge = e.getMaxAge();
        }
    }

    @Data
    private static class CacheConfig implements Serializable {
        private boolean allowCache;
        private Set<String> cacheControl;

        private Long expires;

        private Long maxAge;

        private Long smaxAge;

        private String vary;

        private boolean etag;

        private boolean weakValidation;

        public CacheConfig(CacheProfile cacheProfile) {
            allowCache = cacheProfile.isAllowCache();
            cacheControl = cacheProfile.getCacheControl().stream().map(e -> e.label).collect(Collectors.toSet());
            expires = cacheProfile.getExpires();
            maxAge = cacheProfile.getMaxAge();
            smaxAge = cacheProfile.getSmaxAge();
            vary = cacheProfile.getVary();
            etag = cacheProfile.isEtag();
            weakValidation = cacheProfile.isWeakValidation();
        }
    }
}
