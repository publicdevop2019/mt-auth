package com.mt.access.domain.model.verification_code;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.Code;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.verification_code.event.VerificationCodeCreated;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.validate.Checker;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeService {

    public DomainId createOrUpdate(
        ClientId clientId, RegistrationEmail email,
        Code code, TransactionContext context
    ) {
        Optional<VerificationCode> query =
            DomainRegistry.getVerificationCodeRepository().query(email);
        if (query.isEmpty()) {
            VerificationCode code1 = new VerificationCode(email, code);
            DomainRegistry.getVerificationCodeRepository().add(clientId, code1);
            context
                .append(new VerificationCodeCreated(email));
            context
                .append(new VerificationCodeUpdated(email, code));
            return code1.getDomainId();
        } else {
            DomainRegistry.getVerificationCodeRepository().updateCode(clientId, email,
                code);
            return query.get().getDomainId();
        }
    }

    public DomainId createOrUpdate(ClientId clientId,
                                            RegistrationMobile mobile, Code code,
                                            TransactionContext context) {
        Optional<VerificationCode> query =
            DomainRegistry.getVerificationCodeRepository().query(mobile);
        if (query.isEmpty()) {
            VerificationCode code1 = new VerificationCode(mobile, code);
            DomainRegistry.getVerificationCodeRepository().add(clientId, code1);
            context
                .append(new VerificationCodeCreated(mobile));
            context
                .append(new VerificationCodeUpdated(mobile, code));
            return code1.getDomainId();
        } else {
            DomainRegistry.getVerificationCodeRepository().updateCode(clientId, mobile,
                code);
            return query.get().getDomainId();
        }
    }

    public boolean checkCodeUsing(RegistrationEmail email, Code code) {
        Optional<VerificationCode> pendingUser1 =
            DomainRegistry.getVerificationCodeRepository().query(email);
        if (pendingUser1.isEmpty()) {
            throw new DefinedRuntimeException("verification code not found, maybe not click send?",
                "1026",
                HttpResponseCode.BAD_REQUEST);
        } else {
            VerificationCode verificationCode = pendingUser1.get();
            return verificationCode.getCode().equals(code);
        }
    }

    public boolean checkCodeUsing(RegistrationMobile userMobile, Code code) {
        Optional<VerificationCode> pendingUser1 =
            DomainRegistry.getVerificationCodeRepository().query(userMobile);
        if (pendingUser1.isEmpty()) {
            throw new DefinedRuntimeException("verification code not found, maybe not click send?",
                "1026",
                HttpResponseCode.BAD_REQUEST);
        } else {
            VerificationCode verificationCode = pendingUser1.get();
            return verificationCode.getCode().equals(code);
        }
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
