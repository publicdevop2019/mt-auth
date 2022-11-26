package com.mt.proxy.domain;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Set;
import java.util.TreeSet;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * this requires to be same as EndpointProxyCardRepresentation
 * from mt0-access in order to generate same MD5 value.
 */
@Getter
@Setter
@NoArgsConstructor
public class Endpoint implements Serializable, Comparable<Endpoint> {
    private String id;
    private String description;
    private String resourceId;
    private String projectId;
    private String path;
    private String method;

    private boolean websocket;
    private boolean csrfEnabled;
    private boolean secured;
    private CorsConfig corsConfig;
    private CacheConfig cacheConfig;
    private String permissionId;
    private TreeSet<Subscription> subscriptions;

    public boolean allowAccess(String jwtRaw) throws ParseException {
        if (secured && permissionId == null) {
            return false;
        }
        if (!secured && permissionId == null) {
            return true;
        }
        Set<String> roles = DomainRegistry.getJwtService().getPermissionIds(jwtRaw);
        return roles.contains(permissionId);
    }

    public boolean hasCorsInfo() {
        return getCorsConfig() != null;
    }

    public boolean hasCacheInfo() {
        return getCacheConfig() != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Endpoint)) {
            return false;
        }
        Endpoint endpoint = (Endpoint) o;
        return Objects.equal(id, endpoint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }


    @Override
    public int compareTo(Endpoint o) {
        return this.getId().compareTo(o.getId());
    }

    public Subscription getSelfSubscription() {
        return this.subscriptions.stream().filter(e -> e.getProjectId().equals(this.projectId))
            .findFirst().orElse(null);
    }

    @Getter
    public static class CorsConfig implements Serializable {
        private Set<String> origin;
        private boolean credentials;
        private Set<String> allowedHeaders;
        private Set<String> exposedHeaders;
        private Long maxAge;


        public CorsConfig() {
        }
    }

    @Data
    public static class CacheConfig implements Serializable {
        private boolean allowCache;
        private Set<String> cacheControl;

        private Long expires;

        private Long maxAge;

        private Long smaxAge;

        private String vary;

        private boolean etag;

        private boolean weakValidation;


        public CacheConfig() {
        }
    }

    @Data
    public static class Subscription implements Serializable, Comparable<Subscription>{
        private String projectId;
        private int replenishRate;
        private int burstCapacity;

        public Subscription() {
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Subscription that = (Subscription) o;
            return java.util.Objects.equals(projectId, that.projectId) &&
                java.util.Objects.equals(replenishRate, that.replenishRate) &&
                java.util.Objects.equals(burstCapacity, that.burstCapacity);
        }

        @Override
        public int hashCode() {
            return java.util.Objects.hash(projectId, replenishRate, burstCapacity);
        }

        @Override
        public int compareTo(Subscription o) {
            return this.getProjectId().compareTo(o.getProjectId());
        }
    }
}
