package com.mt.access.application.cors_profile.command;

import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.cors_profile.Origin;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CorsProfilePatchCommand {
    private boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private String name;
    private String description;
    private Long maxAge;

    public CorsProfilePatchCommand(CorsProfile corsProfile1) {
        this.allowCredentials = corsProfile1.isAllowCredentials();
        this.allowedHeaders = corsProfile1.getAllowedHeaders();
        this.allowOrigin = corsProfile1.getAllowOrigin().stream().map(Origin::getValue)
            .collect(Collectors.toSet());
        this.exposedHeaders = corsProfile1.getExposedHeaders();
        this.maxAge = corsProfile1.getMaxAge();
        this.name = corsProfile1.getName();
        this.description = corsProfile1.getDescription();
    }
}
