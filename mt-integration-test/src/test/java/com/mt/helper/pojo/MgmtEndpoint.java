package com.mt.helper.pojo;

import java.util.Set;
import lombok.Data;

@Data
public class MgmtEndpoint {
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

    @Data
    private static class CorsConfig {
        private Set<String> origin;
        private Boolean credentials;
        private Set<String> allowedHeaders;
        private Set<String> exposedHeaders;
        private Long maxAge;

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

    }
}
