package com.mt.access.application.cache_profile.representation;

import com.mt.access.domain.model.cache_profile.CacheProfile;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class CacheProfileCardRepresentation {
    private String name;
    private String description;
    private String id;
    private Set<String> cacheControl;
    private Long expires;
    private Long maxAge;
    private Long smaxAge;
    private String vary;
    private boolean allowCache;
    private boolean etag;
    private boolean weakValidation;

    public CacheProfileCardRepresentation(CacheProfile profile) {
        this.name = profile.getName();
        this.id = profile.getCacheProfileId().getDomainId();
        this.description = profile.getDescription();
        this.cacheControl =
            profile.getCacheControl().stream().map(e -> e.label).collect(Collectors.toSet());
        this.expires = profile.getExpires();
        this.maxAge = profile.getMaxAge();
        this.vary = profile.getVary();
        this.smaxAge = profile.getSmaxAge();
        this.etag = profile.isEtag();
        this.weakValidation = profile.isWeakValidation();
        this.allowCache = profile.isAllowCache();
    }
}
