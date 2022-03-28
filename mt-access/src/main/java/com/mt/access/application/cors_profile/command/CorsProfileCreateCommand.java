package com.mt.access.application.cors_profile.command;

import java.util.Set;
import lombok.Data;

@Data
public class CorsProfileCreateCommand {
    private String name;
    private String description;
    private boolean allowCredentials;
    private Set<String> allowedHeaders;
    private Set<String> allowOrigin;
    private Set<String> exposedHeaders;
    private Long maxAge;
}
