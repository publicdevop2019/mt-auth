package com.mt.proxy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Set;
import java.util.SortedSet;
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
    private SortedSet<Subscription> subscriptions;

    public void sortSubscription() {
        SortedSet<Subscription> objects = new TreeSet<>();
        subscriptions.stream().sorted().forEach(objects::add);
        subscriptions = objects;
    }

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

    @JsonIgnore//avoid serialization problem
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
    public static class Subscription implements Serializable, Comparable<Subscription> {
        private String projectId;
        private int replenishRate;
        private int burstCapacity;

        public Subscription() {
        }

        @Override
        public int compareTo(Subscription o) {
            return this.getProjectId().compareTo(o.getProjectId());
        }
    }
}
