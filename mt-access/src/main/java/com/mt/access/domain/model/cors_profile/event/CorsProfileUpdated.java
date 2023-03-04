package com.mt.access.domain.model.cors_profile.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class CorsProfileUpdated extends DomainEvent {

    public static final String CORS_PROFILE_UPDATED = "cors_profile_updated";
    public static final String name = "CORS_PROFILE_UPDATED";

    {
        setTopic(CORS_PROFILE_UPDATED);
        setName(name);

    }

    public CorsProfileUpdated(CorsProfile corsProfile) {
        super(corsProfile.getCorsId());
    }
}