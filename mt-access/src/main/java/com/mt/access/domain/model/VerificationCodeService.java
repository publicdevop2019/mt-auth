package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.verification_code.RegistrationEmail;
import com.mt.access.domain.model.verification_code.RegistrationMobile;
import com.mt.access.domain.model.verification_code.VerificationCode;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Utility;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    public void sendCode(
        ClientId clientId,
        RegistrationEmail email,
        TransactionContext context
    ) {
        DomainRegistry.getCoolDownService()
            .hasCoolDown(email.getDomainId(), OperationType.VERIFICATION_CODE);
        VerificationCode verificationCode = new VerificationCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(clientId, verificationCode.getValue(), VerificationCode.OPERATION_TYPE,
                email.getDomainId());
        context
            .append(new VerificationCodeUpdated(email, verificationCode));
    }

    public void sendCode(
        ClientId clientId,
        RegistrationMobile mobile,
        TransactionContext context
    ) {
        DomainRegistry.getCoolDownService()
            .hasCoolDown(mobile.getDomainId(), OperationType.VERIFICATION_CODE);
        VerificationCode verificationCode = new VerificationCode();
        DomainRegistry.getTemporaryCodeService()
            .issueCode(clientId, verificationCode.getValue(), VerificationCode.OPERATION_TYPE,
                mobile.getDomainId());
        context
            .append(new VerificationCodeUpdated(mobile, verificationCode));
    }

    public void validateCreate(String countryCode, String mobileNumber, String email) {
        if (Utility.isNull(email) && Utility.isNull(countryCode) && Utility.isNull(mobileNumber)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }

        if (Utility.notNull(email) &&
            (Utility.notNull(countryCode) || Utility.notNull(mobileNumber))) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        if (Utility.notNull(countryCode) && Utility.isNull(mobileNumber)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        if (Utility.isNull(countryCode) && Utility.notNull(mobileNumber)) {
            throw new DefinedRuntimeException("invalid params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
    }


}
