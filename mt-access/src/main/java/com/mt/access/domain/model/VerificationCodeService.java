package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.access.domain.model.verification_code.VerificationCode;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    public void sendCode(
        ClientId clientId,
        RegistrationEmail email,
        SupportedLocale locale,
        TransactionContext context
    ) {
        DomainRegistry.getCoolDownService()
            .hasCoolDown(email.getDomainId(), OperationType.VERIFICATION_CODE);
        VerificationCode verificationCode = new VerificationCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(clientId, verificationCode.getValue(), VerificationCode.OPERATION_TYPE,
                email.getDomainId());
        context
            .append(new VerificationCodeUpdated(email, verificationCode, locale));
    }

    public void sendCode(
        ClientId clientId,
        RegistrationMobile mobile,
        SupportedLocale locale,
        TransactionContext context
    ) {
        DomainRegistry.getCoolDownService()
            .hasCoolDown(mobile.getDomainId(), OperationType.VERIFICATION_CODE);
        VerificationCode verificationCode = new VerificationCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(clientId, verificationCode.getValue(), VerificationCode.OPERATION_TYPE,
                mobile.getDomainId());
        context
            .append(new VerificationCodeUpdated(mobile, verificationCode, locale));
    }

    public void validateCreate(String countryCode, String mobileNumber, String email) {
        if (Checker.isNull(email) && Checker.isNull(countryCode) && Checker.isNull(mobileNumber)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }

        if (Checker.notNull(email) &&
            (Checker.notNull(countryCode) || Checker.notNull(mobileNumber))) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        if (Checker.notNull(countryCode) && Checker.isNull(mobileNumber)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        if (Checker.isNull(countryCode) && Checker.notNull(mobileNumber)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
    }


}
