package com.mt.access.domain.model.audit;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Entity
@NoArgsConstructor
public class AuditRecord {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    private String actionName;
    @Column(columnDefinition = "TEXT")
    private String detail;
    @CreatedDate
    @Getter
    private Long actionAt;
    private String actionBy;

    public AuditRecord(String actionName, String actionBy, String detail) {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.actionName = actionName;
        this.actionAt = Instant.now().toEpochMilli();
        this.actionBy = actionBy;
        this.detail = detail;
    }
}
