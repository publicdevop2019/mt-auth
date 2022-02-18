package com.mt.access.domain.model.cache_profile.event;

import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CacheProfileUpdated extends DomainEvent {

    public static final String CACHE_PROFILE_UPDATED = "cache_profile_updated";
    public static final String name ="CACHE_PROFILE_UPDATED" ;
    public CacheProfileUpdated(CacheProfile cacheProfile) {
        super(cacheProfile.getCacheProfileId());
        setTopic(CACHE_PROFILE_UPDATED);
        setName(name);
    }
}
