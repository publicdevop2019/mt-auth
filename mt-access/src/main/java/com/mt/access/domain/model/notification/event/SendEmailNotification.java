package com.mt.access.domain.model.notification.event;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.user.event.UserMfaNotification;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SendEmailNotification extends DomainEvent {
    public static final String SEND_EMAIL_NOTIFICATION_EVENT =
        "send_email_notification_event";
    public static final String name = "SEND_EMAIL_NOTIFICATION_EVENT";
    public static final String FILE_SUFFIX = ".html";
    private String templateUrl;
    private String subject;
    private String email;
    private SupportedLocale locale;
    private Map<String, String> params;

    {
        setTopic(SEND_EMAIL_NOTIFICATION_EVENT);
        setName(name);
    }

    private SendEmailNotification(Notification notification) {
        super(notification.getNotificationId());
    }

    public SendEmailNotification(UserPwdResetCodeUpdated event, Notification notification) {
        this(notification);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("token", event.getCode());
        params = stringStringHashMap;
        email = event.getEmail();
        templateUrl = "PasswordReset_" + event.getLocale().fileSuffix + FILE_SUFFIX;
        subject = DomainRegistry.getI18nService()
            .getI18nValue("email_pwd_reset_token_subject", event.getLocale());
        locale = event.getLocale();
    }

    public SendEmailNotification(VerificationCodeUpdated event,
                                 Notification notification) {
        this(notification);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("code", event.getCode());
        email = event.getEmail();
        params = stringStringHashMap;
        templateUrl = "VerificationCode_" + event.getLocale().fileSuffix + FILE_SUFFIX;
        subject = DomainRegistry.getI18nService()
            .getI18nValue("email_login_verification_subject", event.getLocale());
        locale = event.getLocale();
    }

    public SendEmailNotification(UserMfaNotification event,
                                 Notification notification) {
        this(notification);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("code", event.getCode().getValue());
        email = event.getEmail();
        params = stringStringHashMap;
        templateUrl = "VerificationCode_" + event.getLocale().fileSuffix + FILE_SUFFIX;
        subject = DomainRegistry.getI18nService()
            .getI18nValue("email_login_verification_subject", event.getLocale());
        locale = event.getLocale();
    }

    public SendEmailNotification(CrossDomainValidationFailureCheck event,
                                 Notification notification) {
        this(notification);
        email = event.getEmail();
        templateUrl = "AdminNotification" + FILE_SUFFIX;
        subject = "Application Validation Failed";
    }
}
