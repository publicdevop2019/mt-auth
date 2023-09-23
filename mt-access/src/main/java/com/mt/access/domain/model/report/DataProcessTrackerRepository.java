package com.mt.access.domain.model.report;

import java.util.Optional;

public interface DataProcessTrackerRepository {
    Optional<DataProcessTracker> get();

    void update(DataProcessTracker tracker);

    void add(DataProcessTracker tracker);
}
