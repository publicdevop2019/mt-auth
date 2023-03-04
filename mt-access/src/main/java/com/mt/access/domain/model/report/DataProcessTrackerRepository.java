package com.mt.access.domain.model.report;

import java.util.Set;

public interface DataProcessTrackerRepository {
    DataProcessTracker getTracker();

    void updateTracker(DataProcessTracker tracker,
                       Set<RawAccessRecord> requests);
}
