package com.mt.access.domain.model.pending_user.event;

import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PendingUserActivationCodeUpdated extends DomainEvent {
    public static final String PENDING_USER_ACTIVATION_CODE_UPDATED =
        "pending_user_activation_code_updated";
    public static final String name = "PENDING_USER_ACTIVATION_CODE_UPDATED";
    private String email;
    private String code;

    {

        setName(name);
        setTopic(PENDING_USER_ACTIVATION_CODE_UPDATED);
    }

    public PendingUserActivationCodeUpdated(RegistrationEmail registrationEmail,
                                            ActivationCode activationCode) {
        super(registrationEmail);
        setEmail(registrationEmail);
        setCode(activationCode);
        setInternal(false);
    }

    private void setEmail(RegistrationEmail registrationEmail) {
        this.email = registrationEmail.getDomainId();
    }

    private void setCode(ActivationCode activationCode) {
        this.code = activationCode.getActivationCode();
    }
}
