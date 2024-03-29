package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class UserPasswordChanged extends DomainEvent {

    public static final String USER_PASSWORD_CHANGED = "user_password_changed";
    public static final String name = "USER_PASSWORD_CHANGED";

    {
        setTopic(USER_PASSWORD_CHANGED);
        setName(name);

    }

    public UserPasswordChanged(UserId userId) {
        super(userId);
    }
}
