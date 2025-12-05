package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.user.PwdResetCode;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserMobile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.validate.Checker;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPwdResetCodeUpdated extends DomainEvent {
    public static final String USER_PWD_RESET_CODE_UPDATED = "user_pwd_reset_code_updated";
    public static final String name = "USER_PWD_RESET_CODE_UPDATED";
    private String email;
    private String countryCode;
    private String mobileNumber;
    private String code;
    private SupportedLocale locale;

    {
        setName(name);
        setTopic(USER_PWD_RESET_CODE_UPDATED);

    }

    public UserPwdResetCodeUpdated(UserId userId, UserEmail email,
                                   PwdResetCode pwdResetToken, SupportedLocale locale) {
        super(userId);
        setEmail(email);
        setCode(pwdResetToken);
        this.locale = locale;
    }

    public UserPwdResetCodeUpdated(UserId userId, UserMobile mobile,
                                   PwdResetCode pwdResetToken, SupportedLocale locale) {
        super(userId);
        countryCode = mobile.getCountryCode();
        mobileNumber = mobile.getMobileNumber();
        setCode(pwdResetToken);
        this.locale = locale;
    }

    public void setEmail(UserEmail userEmail) {
        if (Checker.notNull(userEmail)) {
            this.email = userEmail.getEmail();
        }
    }

    public void setCode(PwdResetCode pwdResetCode) {
        this.code = pwdResetCode.getValue();
    }
}
