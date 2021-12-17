package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserUpdated extends DomainEvent {

    public static final String USER_UPDATED = "user_updated";
    public static final String name = "USER_UPDATED";
    public UserUpdated(UserId userId) {
        super(userId);
        setTopic(USER_UPDATED);
        setName(name);
    }
}
