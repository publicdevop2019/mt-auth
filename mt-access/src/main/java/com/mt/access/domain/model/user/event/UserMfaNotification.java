package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.user.MfaCode;
import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.validate.Checker;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserMfaNotification extends DomainEvent {

    public static final String USER_MFA_NOTIFICATION = "user_mfa_notification";
    public static final String name = "USER_MFA_NOTIFICATION";
    private String mobileNumber;
    private String countryCode;
    private String email;
    private MfaDeliverMethod deliverMethod;

    private MfaCode code;
    private SupportedLocale locale;

    {
        setTopic(USER_MFA_NOTIFICATION);
        setName(name);
    }

    public UserMfaNotification(User user, MfaCode mfaCode) {
        super(user.getUserId());
        code = mfaCode;
        if (Checker.notNull(user.getEmail())) {
            email = user.getEmail().getEmail();
            deliverMethod = MfaDeliverMethod.EMAIL;
        } else {
            mobileNumber = user.getMobile().getMobileNumber();
            countryCode = user.getMobile().getCountryCode();
            deliverMethod = MfaDeliverMethod.MOBILE;
        }
        this.locale = SupportedLocale.parseUserLang(user.getLanguage());
    }

    public UserMfaNotification(User user, MfaCode code, MfaDeliverMethod method) {
        super(user.getUserId());
        this.code = code;
        if (MfaDeliverMethod.EMAIL.equals(method)) {
            email = user.getEmail().getEmail();
            deliverMethod = MfaDeliverMethod.EMAIL;
        } else {
            mobileNumber = user.getMobile().getMobileNumber();
            countryCode = user.getMobile().getCountryCode();
            deliverMethod = MfaDeliverMethod.MOBILE;
        }
        this.locale = SupportedLocale.parseUserLang(user.getLanguage());
    }
}
