package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class NewUserRegistered extends DomainEvent {

    public static final String USER_CREATED = "user_created";
    public static final String name = "USER_CREATED";
    @Getter
    private UserEmail email;

    {

        setTopic(USER_CREATED);
        setName(name);
    }

    public NewUserRegistered(UserId userId, UserEmail email) {
        super(userId);
        this.email = email;
    }
}
