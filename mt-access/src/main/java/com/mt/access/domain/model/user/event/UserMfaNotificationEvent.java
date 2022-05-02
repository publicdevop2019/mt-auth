package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class UserMfaNotificationEvent extends DomainEvent {
    public UserMfaNotificationEvent(User user) {

    }
}
