package com.mt.access.domain.model.cors_profile.event;

import com.mt.access.domain.model.cors_profile.CORSProfile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CORSProfileRemoved extends DomainEvent {

    public static final String CORS_PROFILE_REMOVED = "cors_profile_removed";
    public static final String name = "CORS_PROFILE_REMOVED";
    public CORSProfileRemoved(CORSProfile corsProfile){
        super(corsProfile.getCorsId());
        setTopic(CORS_PROFILE_REMOVED);
        setName(name);
    }
}
