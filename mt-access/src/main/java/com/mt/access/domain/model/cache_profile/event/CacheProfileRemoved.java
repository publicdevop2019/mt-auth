package com.mt.access.domain.model.cache_profile.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class CacheProfileRemoved extends DomainEvent {
    public static final String CACHE_PROFILE_REMOVED = "cache_profile_removed";
    public static final String name = "CACHE_PROFILE_REMOVED";

    {
        setTopic(CACHE_PROFILE_REMOVED);
        setName(name);
    }

    public CacheProfileRemoved(CacheProfile cacheProfile) {
        super(cacheProfile.getCacheProfileId());
    }
}
