package com.mt.access.domain.model.cors_profile.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class CorsProfileRemoved extends DomainEvent {

    public static final String CORS_PROFILE_REMOVED = "cors_profile_removed";
    public static final String name = "CORS_PROFILE_REMOVED";

    public CorsProfileRemoved(CorsProfile corsProfile) {
        super(corsProfile.getCorsId());
        setTopic(CORS_PROFILE_REMOVED);
        setName(name);
    }
}
