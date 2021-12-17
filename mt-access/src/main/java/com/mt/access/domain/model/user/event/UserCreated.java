package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserCreated extends DomainEvent {

    public static final String USER_CREATED = "user_created";
    public static final String name = "USER_CREATED";
    public UserCreated(UserId userId) {
        super(userId);
        setTopic(USER_CREATED);
        setName(name);
    }
}
