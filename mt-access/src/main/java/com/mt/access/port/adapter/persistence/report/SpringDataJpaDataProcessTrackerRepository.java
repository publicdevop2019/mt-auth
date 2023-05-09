package com.mt.access.port.adapter.persistence.report;

import static org.slf4j.LoggerFactory.getLogger;

import com.mt.access.domain.model.report.DataProcessTracker;
import com.mt.access.domain.model.report.DataProcessTrackerRepository;
import com.mt.access.domain.model.report.RawAccessRecord;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaDataProcessTrackerRepository extends DataProcessTrackerRepository,
    JpaRepository<DataProcessTracker, Long> {

    default DataProcessTracker get() {
        Iterable<DataProcessTracker> all = findAll();
        List<DataProcessTracker> objects = new ArrayList<>(1);
        all.forEach(objects::add);
        return objects.isEmpty() ? new DataProcessTracker() : objects.get(0);
    }

    default void update(DataProcessTracker tracker,
                        Set<RawAccessRecord> requests) {
        Optional<Long> reduce = requests.stream().map(RawAccessRecord::getId).reduce(Math::max);
        reduce.ifPresent(tracker::setLastProcessedId);
        LogHolder.LOGGER
            .debug("etl job summary, next tracking id {}", tracker.getLastProcessedId());
        save(tracker);
    }

    final class LogHolder {
        static final Logger LOGGER = getLogger(SpringDataJpaDataProcessTrackerRepository.class);
    }
}
