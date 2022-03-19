package com.mt.proxy.domain;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Set;

/**
 * this requires to be same as EndpointProxyCardRepresentation from mt0-access in order to generate same MD5 value
 */
@Getter
@Setter
@NoArgsConstructor
public class Endpoint implements Serializable, Comparable<Endpoint> {
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
    private String permissionId;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Endpoint)) return false;
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
}
