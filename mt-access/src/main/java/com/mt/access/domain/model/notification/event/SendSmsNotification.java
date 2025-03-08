package com.mt.access.domain.model.notification.event;

import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.user.event.UserMfaNotification;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendSmsNotification extends DomainEvent {
    public static final String SEND_SMS_NOTIFICATION_EVENT =
        "send_sms_notification_event";
    public static final String name = "SEND_SMS_NOTIFICATION_EVENT";
    private String mobileNumber;
    private String countryCode;
    private String code;

    {
        setTopic(SEND_SMS_NOTIFICATION_EVENT);
        setName(name);

    }

    public SendSmsNotification(UserMfaNotification event, Notification notification) {
        super(notification.getNotificationId());
        mobileNumber = event.getMobileNumber();
        countryCode = event.getCountryCode();
        code = event.getCode().getValue();
    }

    public SendSmsNotification(VerificationCodeUpdated event, Notification notification) {
        super(notification.getNotificationId());
        mobileNumber = event.getMobileNumber();
        countryCode = event.getCountryCode();
        code = event.getCode();
    }

    public SendSmsNotification(UserPwdResetCodeUpdated event, Notification notification) {
        super(notification.getNotificationId());
        mobileNumber = event.getMobileNumber();
        countryCode = event.getCountryCode();
        code = event.getCode();
    }
}
