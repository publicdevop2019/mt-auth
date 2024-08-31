package com.mt.access.domain.model.verification_code;

import com.mt.access.domain.model.activation_code.Code;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class VerificationCode extends Auditable {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private DomainId domainId;

    @Getter
    private Code code;

    public VerificationCode(RegistrationEmail email, Code code) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setDomainId(email);
        setCode(code);
    }

    public VerificationCode(RegistrationMobile mobile, Code code) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setDomainId(mobile);
        setCode(code);
    }

    public static VerificationCode fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                                   Long modifiedAt, String modifiedBy,
                                                   Integer version,
                                                   Code code,
                                                   AnyDomainId domainId) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setId(id);
        verificationCode.setCreatedAt(createdAt);
        verificationCode.setCreatedBy(createdBy);
        verificationCode.setModifiedAt(modifiedAt);
        verificationCode.setModifiedBy(modifiedBy);
        verificationCode.setVersion(version);
        verificationCode.setCode(code);
        verificationCode.setDomainId(domainId);
        return verificationCode;
    }

    private void setCode(Code code) {
        this.code = code;
    }
}
