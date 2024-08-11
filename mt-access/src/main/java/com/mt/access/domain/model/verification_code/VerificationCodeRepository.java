package com.mt.access.domain.model.verification_code;

import com.mt.access.domain.model.activation_code.Code;
import com.mt.access.domain.model.client.ClientId;
import java.util.Optional;

public interface VerificationCodeRepository {
    Optional<VerificationCode> query(RegistrationEmail email);

    void add(ClientId clientId, VerificationCode verificationCode);

    void updateCode(ClientId clientId, RegistrationEmail email,
                    Code code);

    void updateCode(ClientId clientId, RegistrationMobile mobile, Code code);

    Optional<VerificationCode> query(RegistrationMobile userMobile);

}
