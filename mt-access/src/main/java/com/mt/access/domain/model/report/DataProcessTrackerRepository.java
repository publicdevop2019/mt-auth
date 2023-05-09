package com.mt.access.domain.model.report;

import java.util.Set;

public interface DataProcessTrackerRepository {
    DataProcessTracker get();

    void update(DataProcessTracker tracker,
                Set<RawAccessRecord> requests);
}
