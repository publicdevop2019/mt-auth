package com.mt.access.domain.model.pending_user.event;

import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class PendingUserCreated extends DomainEvent {
    public static final String name = "PENDING_USER_CREATED";
    public static final String PENDING_USER_CREATED = "pending_user_created";

    public PendingUserCreated(RegistrationEmail email) {

        super(email);
        setName(name);
        setTopic(PENDING_USER_CREATED);
    }
}
