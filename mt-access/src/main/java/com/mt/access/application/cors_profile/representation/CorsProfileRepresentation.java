package com.mt.access.application.cors_profile.representation;

import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.common.domain.model.validate.Utility;
import java.util.Set;
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

    public CorsProfileRepresentation(CorsProfile corsProfile, Set<String> allowedHeaders,
                                     Set<String> exposedHeaders, Set<Origin> origin) {
        this.allowCredentials = corsProfile.getAllowCredentials();
        this.allowedHeaders = allowedHeaders;
        this.allowOrigin = Utility.mapToSet(origin, Origin::getValue);
        this.exposedHeaders = exposedHeaders;
        this.maxAge = corsProfile.getMaxAge();
        this.name = corsProfile.getName();
        this.id = corsProfile.getCorsId().getDomainId();
        this.description = corsProfile.getDescription();
        this.version = corsProfile.getVersion();
    }
}
