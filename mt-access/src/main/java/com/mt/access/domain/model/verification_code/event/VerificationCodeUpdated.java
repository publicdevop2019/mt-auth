package com.mt.access.domain.model.verification_code.event;

import com.mt.access.domain.model.activation_code.Code;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.common.domain.model.domain_event.DomainEvent;
import com.mt.common.domain.model.validate.Checker;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class VerificationCodeUpdated extends DomainEvent {
    public static final String VERIFICATION_CODE_UPDATED =
        "verification_code_updated";
    public static final String name = "VERIFICATION_CODE_UPDATED";
    private String email;
    private String countryCode;
    private String mobileNumber;

    private void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    private void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    private String code;

    {
        setName(name);
        setTopic(VERIFICATION_CODE_UPDATED);
    }

    public VerificationCodeUpdated(RegistrationEmail registrationEmail,
                                   Code code) {
        super(registrationEmail);
        setEmail(registrationEmail);
        setCode(code);
    }

    public VerificationCodeUpdated(RegistrationMobile mobile, Code code) {
        super(mobile);
        setCountryCode(mobile.getCountryCode());
        setMobileNumber(mobile.getMobileNumber());
        setCode(code);
    }

    private void setEmail(RegistrationEmail registrationEmail) {
        if (Checker.notNull(registrationEmail)) {
            this.email = registrationEmail.getDomainId();
        }
    }

    private void setCode(Code code) {
        this.code = code.getValue();
    }
}
