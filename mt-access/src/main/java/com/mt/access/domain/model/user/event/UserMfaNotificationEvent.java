package com.mt.access.domain.model.user.event;

import com.mt.access.domain.model.user.MfaCode;
import com.mt.access.domain.model.user.MfaInfo;
import com.mt.access.domain.model.user.User;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserMfaNotificationEvent extends DomainEvent {

    public static final String USER_MFA_NOTIFICATION = "user_mfa_notification";
    public static final String name = "USER_MFA_NOTIFICATION";
    private String mobile;

    private MfaCode code;

    public UserMfaNotificationEvent(User user, MfaInfo mfaInfo) {
        super(user.getUserId());
        code = mfaInfo.getCode();
        mobile = user.getMobile().value();
        setTopic(USER_MFA_NOTIFICATION);
        setName(name);
    }
}
