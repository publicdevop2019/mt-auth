package com.mt.access.domain.model.cors_profile.event;

import com.mt.access.domain.model.cors_profile.CORSProfile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class CORSProfileUpdated extends DomainEvent {

    public static final String CORS_PROFILE_UPDATED = "cors_profile_updated";
    public static final String name = "CORS_PROFILE_UPDATED";
    public CORSProfileUpdated(CORSProfile corsProfile){
        super(corsProfile.getCorsId());
        setTopic(CORS_PROFILE_UPDATED);
        setName(name);
    }
}