package com.mt.access.domain.model.temporary_code;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemporaryCode extends Auditable {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private AnyDomainId domainId;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String code;

    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String operationType;

    public TemporaryCode(AnyDomainId domainId, String code, String operationType) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setDomainId(domainId);
        setCode(code);
        setOperationType(operationType);
    }

    public static TemporaryCode fromDatabaseRow(Long id, Long createdAt, String createdBy,
                                                Long modifiedAt, String modifiedBy,
                                                Integer version,
                                                String code,
                                                AnyDomainId domainId) {
        TemporaryCode temporaryCode = new TemporaryCode();
        temporaryCode.setId(id);
        temporaryCode.setCreatedAt(createdAt);
        temporaryCode.setCreatedBy(createdBy);
        temporaryCode.setModifiedAt(modifiedAt);
        temporaryCode.setModifiedBy(modifiedBy);
        temporaryCode.setVersion(version);
        temporaryCode.setCode(code);
        temporaryCode.setDomainId(domainId);
        return temporaryCode;
    }
}
