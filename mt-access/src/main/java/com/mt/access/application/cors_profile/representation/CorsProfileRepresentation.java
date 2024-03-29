package com.mt.access.application.cors_profile.representation;

import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.Origin;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class CorsProfileRepresentation {
    private Boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private Long maxAge;
    private String name;
    private String id;
    private String description;
    private Integer version;

    public CorsProfileRepresentation(CorsProfile corsProfile) {
        this.allowCredentials = corsProfile.getAllowCredentials();
        this.allowedHeaders = new HashSet<>();
        this.allowedHeaders.addAll(corsProfile.getAllowedHeaders());
        this.allowOrigin =
            corsProfile.getAllowOrigin().stream().map(Origin::getValue).collect(Collectors.toSet());
        this.exposedHeaders = new HashSet<>();
        this.exposedHeaders.addAll(corsProfile.getExposedHeaders());
        this.maxAge = corsProfile.getMaxAge();
        this.name = corsProfile.getName();
        this.id = corsProfile.getCorsId().getDomainId();
        this.description = corsProfile.getDescription();
        this.version = corsProfile.getVersion();
    }
}
