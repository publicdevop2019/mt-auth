package com.mt.access.domain.model.report;

import com.mt.common.domain.CommonDomainRegistry;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Version;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class DataProcessTracker {
    @Id
    private final Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    @Version
    private int version;
    private long lastProcessedId;
}
