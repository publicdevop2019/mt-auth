package com.mt.access.domain.model.report;

import com.mt.common.domain.CommonDomainRegistry;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DataProcessTracker {
    private Long id = CommonDomainRegistry.getUniqueIdGeneratorService().id();
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
