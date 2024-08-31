package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.access.domain.model.user.UserName;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class NewUserRegistered extends DomainEvent {

    public static final String USER_CREATED = "user_created";
    public static final String name = "USER_CREATED";
    @Getter
    private String registeredUsing;

    {

        setTopic(USER_CREATED);
        setName(name);
    }

    public NewUserRegistered(UserId userId, UserEmail userEmail) {
        super(userId);
        this.registeredUsing = userEmail.getEmail();
    }

    public NewUserRegistered(UserId userId, UserMobile userMobile) {
        super(userId);
        this.registeredUsing = userMobile.value();
    }

    public NewUserRegistered(UserId userId, UserName username) {
        super(userId);
        this.registeredUsing = username.getValue();
    }
}
