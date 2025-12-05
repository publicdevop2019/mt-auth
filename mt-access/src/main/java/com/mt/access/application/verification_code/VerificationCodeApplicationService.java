package com.mt.access.application.verification_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.validate.Checker;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeApplicationService {

    private static final String VERIFICATION_CODE = "VerificationCode";

    public void sendCode(VerificationCodeCreateCommand command, String changeId) {
        DomainRegistry.getVerificationCodeService()
            .validateCreate(command.getCountryCode(), command.getMobileNumber(),
                command.getEmail());
        if (Checker.notNull(command.getEmail())) {
            RegistrationEmail email = new RegistrationEmail(command.getEmail());
            sendCode(email, command.getClientId(), command.getLocale(), changeId);
        } else {
            RegistrationMobile mobile =
                new RegistrationMobile(command.getCountryCode(), command.getMobileNumber());
            sendCode(mobile, command.getClientId(), command.getLocale(), changeId);
        }
    }

    private void sendCode(RegistrationMobile mobile, ClientId clientId, SupportedLocale locale,
                          String changeId) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    DomainRegistry.getVerificationCodeService()
                        .sendCode(clientId, mobile, locale, context);
                    return null;
                }, VERIFICATION_CODE
            );
    }

    private void sendCode(RegistrationEmail email, ClientId clientId, SupportedLocale locale,
                          String changeId) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    DomainRegistry.getVerificationCodeService()
                        .sendCode(clientId, email, locale, context);
                    return null;
                }, VERIFICATION_CODE
            );
    }
}
