package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class UserMfaNotificationEvent extends DomainEvent {

    public static final String USER_MFA_NOTIFICATION = "user_mfa_notification";
    public static final String name = "USER_MFA_NOTIFICATION";

    public UserMfaNotificationEvent(User user) {
        super(user.getUserId());
        setTopic(USER_MFA_NOTIFICATION);
        setName(name);
    }
}
