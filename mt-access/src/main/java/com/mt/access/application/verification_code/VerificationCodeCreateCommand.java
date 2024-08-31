package com.mt.access.application.verification_code;

import com.mt.access.domain.model.client.ClientId;
import lombok.Data;

@Data
public class VerificationCodeCreateCommand {
    private String email;
    private String countryCode;
    private String mobileNumber;
    private ClientId clientId;
}
