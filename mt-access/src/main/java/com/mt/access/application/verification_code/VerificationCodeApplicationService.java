package com.mt.access.application.verification_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.Code;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeApplicationService {

    private static final String VERIFICATION_CODE = "VerificationCode";

    public String create(VerificationCodeCreateCommand command, String changeId) {

        DomainRegistry.getVerificationCodeService()
            .validateCreate(command.getCountryCode(), command.getMobileNumber(),
                command.getEmail());
        if (Checker.notNull(command.getEmail())) {
            RegistrationEmail registrationEmail = new RegistrationEmail(command.getEmail());
            return CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId,
                    (context) -> {
                        DomainRegistry.getCoolDownService().hasCoolDown(registrationEmail.getDomainId(),
                            OperationType.VERIFICATION_CODE);
                        DomainId orUpdatePendingUser =
                            DomainRegistry.getVerificationCodeService()
                                .createOrUpdate(command.getClientId(), registrationEmail,
                                    new Code(), context);
                        return orUpdatePendingUser.getDomainId();
                    }, VERIFICATION_CODE
                );
        } else {
            RegistrationMobile registrationMobile =
                new RegistrationMobile(command.getCountryCode(), command.getMobileNumber());
            return CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId,
                    (context) -> {
                        DomainRegistry.getCoolDownService().hasCoolDown(registrationMobile.getDomainId(),
                            OperationType.VERIFICATION_CODE);
                        DomainId orUpdatePendingUser =
                            DomainRegistry.getVerificationCodeService()
                                .createOrUpdate(command.getClientId(), registrationMobile,
                                    new Code(), context);
                        return orUpdatePendingUser.getDomainId();
                    }, VERIFICATION_CODE
                );
        }
    }

    public void checkCode(String email, String code) {
        if (!DomainRegistry.getVerificationCodeService()
            .checkCodeUsing(new RegistrationEmail(email), new Code(code))) {
            throw new DefinedRuntimeException("code mismatch", "1025",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void checkCode(String countryCode, String mobileNumber, String code) {
        if (!DomainRegistry.getVerificationCodeService()
            .checkCodeUsing(new RegistrationMobile(countryCode, mobileNumber),
                new Code(code))) {
            throw new DefinedRuntimeException("code mismatch", "1025",
                HttpResponseCode.BAD_REQUEST);
        }
    }
}
