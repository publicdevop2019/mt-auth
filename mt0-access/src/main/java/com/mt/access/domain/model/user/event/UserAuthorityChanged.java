package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserAuthorityChanged extends DomainEvent {

    public static final String USER_AUTHORITY_CHANGED = "user_authority_changed";
    public static final String name = "USER_AUTHORITY_CHANGED";
    public UserAuthorityChanged(UserId userId) {
        super(userId);
        setTopic(USER_AUTHORITY_CHANGED);
        setName(name);
    }
}
