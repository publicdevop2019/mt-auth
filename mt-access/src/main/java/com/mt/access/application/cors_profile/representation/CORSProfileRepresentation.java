package com.mt.access.application.cors_profile.representation;

import com.mt.access.domain.model.cors_profile.CORSProfile;
import com.mt.access.domain.model.cors_profile.Origin;
import com.mt.common.domain.model.sql.converter.StringSetConverter;
import lombok.Data;

import javax.persistence.Convert;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CORSProfileRepresentation {
    private boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private Long maxAge;
    private String name;
    private String id;
    private String description;
    public CORSProfileRepresentation(CORSProfile corsProfile) {
        this.allowCredentials=corsProfile.isAllowCredentials();
        this.allowedHeaders=corsProfile.getAllowedHeaders();
        this.allowOrigin=corsProfile.getAllowOrigin().stream().map(Origin::getValue).collect(Collectors.toSet());
        this.exposedHeaders=corsProfile.getExposedHeaders();
        this.maxAge=corsProfile.getMaxAge();
        this.name=corsProfile.getName();
        this.id=corsProfile.getCorsId().getDomainId();
        this.description=corsProfile.getDescription();
    }
}
