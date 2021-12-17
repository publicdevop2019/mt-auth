package com.mt.access.application.cache_profile.command;

import com.mt.access.domain.model.cache_profile.CacheControlValue;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import lombok.Data;

import javax.persistence.Convert;
import javax.persistence.Embedded;
import javax.persistence.Id;
import java.util.Set;
@Data
public class CreateCacheProfileCommand {
    private String name;
    private String description;
    private Set<String> cacheControl;
    private Long expires;
    private Long maxAge;
    private Long smaxAge;
    private String vary;
    private boolean etag;
    private boolean allowCache;
    private boolean weakValidation;
}
