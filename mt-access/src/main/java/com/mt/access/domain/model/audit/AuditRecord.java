package com.mt.access.domain.model.audit;

import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
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
    @Temporal(TemporalType.TIMESTAMP)
    @Getter
    private Date actionAt;
    private String actionBy;

    public AuditRecord(String actionName, String actionBy, String detail) {
        id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.actionName = actionName;
        this.actionAt = Date.from(Instant.now());
        this.actionBy = actionBy;
        this.detail = detail;
    }
}
