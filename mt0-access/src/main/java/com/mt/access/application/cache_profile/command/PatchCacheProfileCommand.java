package com.mt.access.application.cache_profile.command;

import com.mt.access.domain.model.cache_profile.CacheProfile;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PatchCacheProfileCommand {
    private String name;
    private String description;
    private Set<String> cacheControl;
    private Long expires;
    private Long maxAge;
    private Long smaxAge;
    private String vary;
    private boolean etag;
    private boolean weakValidation;

    public PatchCacheProfileCommand(CacheProfile profile) {
        this.name=profile.getName();
        this.description=profile.getDescription();
        this.cacheControl=profile.getCacheControl().stream().map(e->e.label).collect(Collectors.toSet());
        this.expires=profile.getExpires();
        this.maxAge=profile.getMaxAge();
        this.vary=profile.getVary();
        this.smaxAge=profile.getSmaxAge();
        this.etag=profile.isEtag();
        this.weakValidation=profile.isWeakValidation();
    }
}
