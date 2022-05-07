package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserPasswordChanged extends DomainEvent implements AuditEvent {

    public static final String USER_PASSWORD_CHANGED = "user_password_changed";
    public static final String name = "USER_PASSWORD_CHANGED";

    public UserPasswordChanged(UserId userId) {
        super(userId);
        setTopic(USER_PASSWORD_CHANGED);
        setName(name);
    }
}
