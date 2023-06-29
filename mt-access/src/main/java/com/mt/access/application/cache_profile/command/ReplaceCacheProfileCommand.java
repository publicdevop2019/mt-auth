package com.mt.access.application.cache_profile.command;

import java.util.Set;
import lombok.Data;

@Data
public class ReplaceCacheProfileCommand {
    private String id;
    private String name;
    private String description;
    private Set<String> cacheControl;
    private Long expires;
    private Long maxAge;
    private Long smaxAge;
    private String vary;
    private String projectId;
    private Boolean etag;
    private Boolean allowCache;
    private Boolean weakValidation;
}
