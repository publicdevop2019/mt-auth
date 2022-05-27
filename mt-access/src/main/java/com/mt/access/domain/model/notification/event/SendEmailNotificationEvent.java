package com.mt.access.domain.model.notification.event;

import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SendEmailNotificationEvent extends DomainEvent {
    public static final String SEND_EMAIL_NOTIFICATION_EVENT =
        "send_email_notification_event";
    public static final String name = "SEND_EMAIL_NOTIFICATION_EVENT";
    private String templateUrl;
    private String subject;
    private String email;
    private Map<String, String> params;

    {
        setTopic(SEND_EMAIL_NOTIFICATION_EVENT);
        setName(name);
    }

    private SendEmailNotificationEvent(Notification notification) {
        super();
        setDomainId(notification.getNotificationId());
    }

    public SendEmailNotificationEvent(UserPwdResetCodeUpdated event, Notification notification) {
        this(notification);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("token", event.getCode());
        params = stringStringHashMap;
        email = event.getEmail();
        templateUrl = "PasswordResetTemplate.ftl";
        subject = "Your password reset token";
    }

    public SendEmailNotificationEvent(PendingUserActivationCodeUpdated event,
                                      Notification notification) {
        this(notification);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("activationCode", event.getCode());
        email = event.getEmail();
        params = stringStringHashMap;
        templateUrl = "ActivationCodeTemplate.ftl";
        subject = "Your activation code";
    }

    public SendEmailNotificationEvent(CrossDomainValidationFailureCheck event,
                                      Notification notification) {
        this(notification);
        email = event.getEmail();
        templateUrl = "AdminNotification.ftl";
        subject = "Application validation failed";
    }
}
