package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.PasswordResetCode;
import com.mt.access.domain.model.user.UserEmail;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserPwdResetCodeUpdated extends DomainEvent {
    public static final String USER_PWD_RESET_CODE_UPDATED = "user_pwd_reset_code_updated";
    public static final String name = "USER_PWD_RESET_CODE_UPDATED";
    private String email;
    private String code;

    {
        setName(name);
        setTopic(USER_PWD_RESET_CODE_UPDATED);

    }

    public UserPwdResetCodeUpdated(UserId userId, UserEmail email,
                                   PasswordResetCode pwdResetToken) {
        super(userId);
        setEmail(email);
        setCode(pwdResetToken);
    }

    public void setEmail(UserEmail userEmail) {
        this.email = userEmail.getEmail();
    }

    public void setCode(PasswordResetCode passwordResetCode) {
        this.code = passwordResetCode.getValue();
    }
}
