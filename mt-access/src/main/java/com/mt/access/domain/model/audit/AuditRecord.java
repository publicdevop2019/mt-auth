package com.mt.access.domain.model.audit;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class AuditRecord {
    @Setter(AccessLevel.PROTECTED)
    @Getter
    protected Long id;
    @Getter
    private String actionName;
    @Getter
    private String detail;
    @Getter
    private Long actionAt;
    @Getter
    private String actionBy;

    public AuditRecord(String actionName, String actionBy, String detail) {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.actionName = actionName;
        this.actionAt = Instant.now().toEpochMilli();
        this.actionBy = actionBy;
        this.detail = detail;
    }
}
