package com.mt.access.domain.model.notification.event;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;

@Getter
public class SendSmsNotificationEvent extends DomainEvent {
    public static final String SEND_SMS_NOTIFICATION_EVENT =
        "send_sms_notification_event";
    public static final String name = "SEND_SMS_NOTIFICATION_EVENT";
    private final String mobile;
    private final String code;

    public SendSmsNotificationEvent(UserMfaNotificationEvent event, Notification notification) {
        super();
        setTopic(SEND_SMS_NOTIFICATION_EVENT);
        setName(name);
        mobile = event.getMobile();
        code = event.getCode().toString();
        setDomainId(notification.getNotificationId());
    }
}
