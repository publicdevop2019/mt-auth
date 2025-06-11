package com.mt.access.infrastructure;

import com.mt.access.domain.model.user.MfaCodeGenerator;
import com.mt.access.domain.model.user.PwdResetCodeGenerator;
import com.mt.access.domain.model.verification_code.VerificationCodeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RandomCodeGenerator implements
    MfaCodeGenerator,
    PwdResetCodeGenerator,
    VerificationCodeGenerator {
    @Value("${mt.misc.code-generator:#{null}}")
    private String mode;

    @Override
    public String generate() {
        if (mode == null || mode.isBlank() || "fixed".equalsIgnoreCase(mode)) {
            return "123456";
        }
        return RandomUtility.randomNumber(6);
    }
}
