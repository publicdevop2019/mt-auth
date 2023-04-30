package com.mt.proxy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Objects;
import java.io.Serializable;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;

/**
 * mirror of mt-access EndpointProxyCacheRepresentation
 * used to generate same MD5 value
 * use @LinkedHashSet to maintain order so MD5 value can be same
 */
@Data
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
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<Subscription> subscriptions;

    public boolean allowAccess(String jwtRaw, Logger log) throws ParseException {
        if (secured && permissionId == null) {
            log.debug("not pass check due to permissionId missing");
            return false;
        }
        if (!secured && permissionId == null) {
            log.debug("pass check due to public endpoint");
            return true;
        }
        Set<String> permissionIds = DomainRegistry.getJwtService().getPermissionIds(jwtRaw);
        boolean contains = permissionIds.contains(permissionId);
        if (contains) {
            log.debug("pass check due to permissionId match");
        } else {
            log.debug("not pass check due to permissionId mismatch");
        }
        return contains;
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
    @ToString
    public static class CorsConfig implements Serializable {
        @JsonDeserialize(as = LinkedHashSet.class)
        private Set<String> origin;
        private boolean credentials;
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
        private boolean allowCache;
        @JsonDeserialize(as = LinkedHashSet.class)
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
