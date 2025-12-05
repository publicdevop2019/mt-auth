package com.mt.access.application.endpoint.representation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.CacheProfileQuery;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileQuery;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.Router;
import com.mt.access.domain.model.endpoint.RouterId;
import com.mt.access.domain.model.endpoint.RouterQuery;
import com.mt.access.domain.model.sub_request.SubRequest;
import com.mt.access.domain.model.sub_request.SubRequestQuery;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.infrastructure.Utility;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EndpointProxyCacheRepresentation
    implements Serializable, Comparable<EndpointProxyCacheRepresentation> {
    private String id;
    private String description;
    private String routerId;
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
    private transient RouterId rawRouterId;
    @JsonIgnore
    private transient CacheProfileId cacheProfileId;
    private String permissionId;
    private Set<ProjectSubscription> subscriptions;

    public EndpointProxyCacheRepresentation(Endpoint endpoint) {
        this.id = endpoint.getEndpointId().getDomainId();
        this.description = endpoint.getDescription();
        this.websocket = endpoint.getWebsocket();
        this.projectId = endpoint.getProjectId().getDomainId();
        this.path = endpoint.getPath();
        this.method = endpoint.getMethod();
        this.rawRouterId = endpoint.getRouterId();
        this.routerId = endpoint.getRouterId().getDomainId();
        this.secured = endpoint.getSecured();
        this.csrfEnabled = endpoint.getCsrfEnabled();
        this.corsProfileId = endpoint.getCorsProfileId();
        this.cacheProfileId = endpoint.getCacheProfileId();
        //add owner project
        this.subscriptions = new HashSet<>();
        if (Checker.isFalse(endpoint.getWebsocket())) {
            this.subscriptions.add(
                new ProjectSubscription(this.projectId, endpoint.getReplenishRate(),
                    endpoint.getBurstCapacity()));
        }
        this.permissionId =
            endpoint.getPermissionId() == null ? null : endpoint.getPermissionId().getDomainId();
    }

    public static void updateDetail(List<EndpointProxyCacheRepresentation> original) {
        if (!original.isEmpty()) {
            Set<RouterId> routerIds =
                original.stream().map(EndpointProxyCacheRepresentation::getRawRouterId)
                    .collect(Collectors.toSet());
            Set<CacheProfileId> cache =
                original.stream().map(EndpointProxyCacheRepresentation::getCacheProfileId)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            Set<CorsProfileId> cors =
                original.stream().map(EndpointProxyCacheRepresentation::getCorsProfileId)
                    .filter(Objects::nonNull).collect(Collectors.toSet());
            Set<EndpointId> epIds =
                original.stream().map(e -> new EndpointId(e.id)).collect(Collectors.toSet());
            Set<CorsProfile> corsFetched = null;
            Set<CacheProfile> cacheFetched = null;
            Set<SubRequest> suReqFetched = null;
            Set<Router> routerFetched = null;
            if (cors.size() > 0) {
                corsFetched = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getCorsProfileRepository().query(query),
                    CorsProfileQuery.internalQuery(cors));
            }
            if (cache.size() > 0) {
                cacheFetched = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getCacheProfileRepository()
                        .query(query), CacheProfileQuery.internalQuery(cache));
            }
            if (epIds.size() > 0) {
                suReqFetched = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getSubRequestRepository().getSubscription(query),
                    new SubRequestQuery(epIds));
            }
            if (routerIds.size() > 0) {
                routerFetched = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getRouterRepository().query(query),
                    new RouterQuery(routerIds));
            }
            Set<CacheProfile> finalCacheFetched = cacheFetched;
            Set<CorsProfile> finalCorsFetched = corsFetched;
            Set<SubRequest> finalSuReqFetched = suReqFetched;
            Set<Router> finalRouterFetched = routerFetched;
            original.forEach(rep -> {
                if (finalCacheFetched != null) {
                    finalCacheFetched.stream()
                        .filter(e -> e.getCacheProfileId().equals(rep.cacheProfileId)).findFirst()
                        .ifPresent(e -> {
                            rep.cacheConfig = new CacheConfig(e,
                                DomainRegistry.getCacheControlRepository().query(e));
                        });
                }
                if (finalCorsFetched != null) {
                    finalCorsFetched.stream().filter(e -> e.getCorsId().equals(rep.corsProfileId))
                        .findFirst().ifPresent(e -> {
                            Set<String> allowed =
                                DomainRegistry.getCorsAllowedHeaderRepository().query(e);
                            Set<String> exposed =
                                DomainRegistry.getCorsExposedHeaderRepository().query(e);
                            Set<Origin> origins = DomainRegistry.getCorsOriginRepository().query(e);
                            rep.corsConfig = new CorsConfig(e, origins, allowed, exposed);
                        });
                }
                if (finalRouterFetched != null) {
                    finalRouterFetched.stream()
                        .filter(e -> e.getRouterId().equals(rep.rawRouterId))
                        .findFirst().ifPresent(e -> {
                            if (e.getPath() != null) {
                                rep.setPath("/" + e.getPath() + "/" + rep.getPath());
                            }
                        });
                }
                Set<ProjectSubscription> collect = finalSuReqFetched.stream()
                    .filter(e -> e.getEndpointId().getDomainId().equals(rep.getId())).map(
                        ProjectSubscription::new).collect(Collectors.toSet());
                rep.subscriptions.addAll(collect);
                SortedSet<ProjectSubscription> objects = new TreeSet<>();
                rep.subscriptions.stream().sorted().forEach(objects::add);
                rep.subscriptions = objects;
            });

        }
    }

    @Override
    public int compareTo(EndpointProxyCacheRepresentation o) {
        return this.getId().compareTo(o.getId());
    }

    @Data
    private static class CorsConfig implements Serializable {
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
    private static class CacheConfig implements Serializable {
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

    @Data
    private static class ProjectSubscription
        implements Serializable, Comparable<ProjectSubscription> {
        private String projectId;
        private Integer replenishRate;
        private Integer burstCapacity;

        public ProjectSubscription(String projectId, int replenishRate, int burstCapacity) {
            this.projectId = projectId;
            this.replenishRate = replenishRate;
            this.burstCapacity = burstCapacity;
        }

        public ProjectSubscription(SubRequest e) {
            this.projectId = e.getProjectId().getDomainId();
            this.replenishRate = e.getReplenishRate();
            this.burstCapacity = e.getBurstCapacity();
        }

        @Override
        public int compareTo(ProjectSubscription o) {
            return this.getProjectId().compareTo(o.getProjectId());
        }
    }
}
