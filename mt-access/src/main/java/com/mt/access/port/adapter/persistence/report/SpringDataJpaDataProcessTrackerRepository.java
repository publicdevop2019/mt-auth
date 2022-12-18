package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.report.DataProcessTracker;
import com.mt.access.domain.model.report.DataProcessTrackerRepository;
import com.mt.access.domain.model.report.RawAccessRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaDataProcessTrackerRepository extends DataProcessTrackerRepository,
    JpaRepository<DataProcessTracker, Long> {

    default DataProcessTracker getTracker() {
        Iterable<DataProcessTracker> all = findAll();
        List<DataProcessTracker> objects = new ArrayList<>(1);
        all.forEach(objects::add);
        return objects.isEmpty() ? new DataProcessTracker() : objects.get(0);
    }

    default void updateTracker(DataProcessTracker tracker,
                               Set<RawAccessRecord> requests) {
        Optional<Long> reduce = requests.stream().map(RawAccessRecord::getId).reduce(Math::max);
        reduce.ifPresent(tracker::setLastProcessedId);
        save(tracker);
    }
}