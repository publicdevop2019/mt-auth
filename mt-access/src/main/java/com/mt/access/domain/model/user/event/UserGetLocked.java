package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class UserGetLocked extends DomainEvent {

    public static final String USER_GET_LOCKED = "user_get_locked";
    public static final String name = "USER_GET_LOCKED";

    {

        setTopic(USER_GET_LOCKED);
        setName(name);
    }

    public UserGetLocked(UserId userId) {
        super(userId);
    }
}
