package com.mt.access.application.cors_profile.representation;

import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.Origin;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class CorsProfileRepresentation {
    private boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private Long maxAge;
    private String name;
    private String id;
    private String description;
    private int version;

    public CorsProfileRepresentation(CorsProfile corsProfile) {
        this.allowCredentials = corsProfile.isAllowCredentials();
        this.allowedHeaders = corsProfile.getAllowedHeaders();
        this.allowOrigin =
            corsProfile.getAllowOrigin().stream().map(Origin::getValue).collect(Collectors.toSet());
        this.exposedHeaders = corsProfile.getExposedHeaders();
        this.maxAge = corsProfile.getMaxAge();
        this.name = corsProfile.getName();
        this.id = corsProfile.getCorsId().getDomainId();
        this.description = corsProfile.getDescription();
        this.version = corsProfile.getVersion();
    }
}
