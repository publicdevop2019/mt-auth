package com.mt.proxy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * mirror of mt-access EndpointProxyCacheRepresentation
 * used to generate same MD5 value
 * use @LinkedHashSet to maintain order so MD5 value can be same
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class Endpoint implements Serializable, Comparable<Endpoint> {
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
    private String permissionId;
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Subscription> subscriptions;

    public EndpointCheckResult checkAccess(String jwtRaw)
        throws ParseException {
        if (secured && permissionId == null) {
            return EndpointCheckResult.missingPermissionId();
        }
        if (!secured && permissionId == null) {
            return EndpointCheckResult.allowPublic();
        }
        Set<String> permissionIds = DomainRegistry.getJwtService().getPermissionIds(jwtRaw);
        boolean contains = permissionIds.contains(permissionId);
        if (contains) {
            return EndpointCheckResult.permissionIdMatch();
        } else {
            return EndpointCheckResult.permissionIdNotFound();
        }
    }

    public boolean hasCorsInfo() {
        return getCorsConfig() != null;
    }

    public boolean hasCacheInfo() {
        return getCacheConfig() != null;
    }


    @Override
    public int compareTo(Endpoint o) {
        return this.getId().compareTo(o.getId());
    }

    @JsonIgnore//avoid serialization problem
    public Subscription getSelfSubscription() {
        return this.subscriptions.stream().filter(e -> e.getProjectId().equals(this.projectId))
            .findFirst().orElse(null);
    }

    @Getter
    @ToString
    public static class CorsConfig implements Serializable {
        @JsonDeserialize(as = LinkedHashSet.class)
        private Set<String> origin;
        private Boolean credentials;
        @JsonDeserialize(as = LinkedHashSet.class)
        private Set<String> allowedHeaders;
        @JsonDeserialize(as = LinkedHashSet.class)
        private Set<String> exposedHeaders;
        private Long maxAge;


        public CorsConfig() {
        }
    }

    @Data
    public static class CacheConfig implements Serializable {
        private Boolean allowCache;
        @JsonDeserialize(as = LinkedHashSet.class)
        private Set<String> cacheControl;

        private Long expires;

        private Long maxAge;

        private Long smaxAge;

        private String vary;

        private Boolean etag;

        private Boolean weakValidation;


        public CacheConfig() {
        }
    }

    @Data
    public static class Subscription implements Serializable, Comparable<Subscription> {
        private String projectId;
        private Integer replenishRate;
        private Integer burstCapacity;

        public Subscription() {
        }

        @Override
        public int compareTo(Subscription o) {
            return this.getProjectId().compareTo(o.getProjectId());
        }
    }
}
