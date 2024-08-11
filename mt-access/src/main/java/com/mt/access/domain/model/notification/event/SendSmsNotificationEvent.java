package com.mt.access.domain.model.notification.event;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendSmsNotificationEvent extends DomainEvent {
    public static final String SEND_SMS_NOTIFICATION_EVENT =
        "send_sms_notification_event";
    public static final String name = "SEND_SMS_NOTIFICATION_EVENT";
    private String mobile;
    private String code;

    {
        setTopic(SEND_SMS_NOTIFICATION_EVENT);
        setName(name);

    }

    public SendSmsNotificationEvent(UserMfaNotificationEvent event, Notification notification) {
        super(notification.getNotificationId());
        mobile = event.getMobile();
        code = event.getCode().toString();
    }

    public SendSmsNotificationEvent(VerificationCodeUpdated event, Notification notification) {
        super(notification.getNotificationId());
        mobile = event.getCountryCode() + " " + event.getMobileNumber();
        code = event.getCode().toString();
    }
}
