package com.mt.access.domain.model.report;

import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"name", "instanceId", "recordId"}))
@Slf4j
@NoArgsConstructor
@Getter
public class AccessRecord {
    @Id
    @Setter(AccessLevel.PROTECTED)
    @Getter(AccessLevel.PRIVATE)
    protected Long id;
    private String name;
    private String instanceId;
    private String recordId;
    private String record;

    public AccessRecord(String name, String instanceId, String record) {
        this.id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
        this.name = name;
        this.instanceId = instanceId;
        this.recordId = record.split(",")[0].replace("id:", "");
        this.record = record;
    }
}
