package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserDeleted extends DomainEvent {

    public static final String USER_DELETED = "user_deleted";
    public static final String name = "USER_DELETED";

    public UserDeleted(UserId userId) {
        super(userId);
        setTopic(USER_DELETED);
        setName(name);
    }
}
