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
    private Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
    @Version
    private Integer version;
    private Long lastProcessedId;

    public static DataProcessTracker fromDatabaseRow(Long id, Long lastProcessId,
                                                     Integer version) {
        DataProcessTracker tracker = new DataProcessTracker();
        tracker.id = id;
        tracker.setLastProcessedId(lastProcessId);
        tracker.setVersion(version);
        return tracker;
    }
}
