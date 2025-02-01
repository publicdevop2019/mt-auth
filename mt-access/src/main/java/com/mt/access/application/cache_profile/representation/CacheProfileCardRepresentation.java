package com.mt.access.application.cache_profile.representation;

import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.common.infrastructure.Utility;
import java.util.Set;
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
    private Boolean allowCache;
    private Boolean etag;
    private Boolean weakValidation;
    private Integer version;

    public CacheProfileCardRepresentation(CacheProfile profile, Set<CacheControlValue> values) {
        this.name = profile.getName();
        this.id = profile.getCacheProfileId().getDomainId();
        this.description = profile.getDescription();
        this.cacheControl =
            Utility.mapToSet(values, e -> e.label);
        this.expires = profile.getExpires();
        this.maxAge = profile.getMaxAge();
        this.vary = profile.getVary();
        this.smaxAge = profile.getSmaxAge();
        this.etag = profile.getEtag();
        this.weakValidation = profile.getWeakValidation();
        this.allowCache = profile.getAllowCache();
        this.version = profile.getVersion();
    }
}
