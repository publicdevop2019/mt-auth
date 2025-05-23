package com.mt.access.domain.model.report;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RawAccessRecordProcessService {


    public void process(TransactionContext context) {
        log.debug("start of access record ETL job");
        Optional<DataProcessTracker> trackerStored =
            DomainRegistry.getDataProcessTrackerRepository().get();
        DataProcessTracker tracker;
        boolean newTracker = false;
        if (trackerStored.isEmpty()) {
            tracker = new DataProcessTracker();
            newTracker = true;
        } else {
            tracker = trackerStored.get();
            newTracker = false;
        }
        log.debug("last process id is {}", tracker.getLastProcessedId());
        Set<RawAccessRecord> totalRecords =
            DomainRegistry.getRawAccessRecordRepository()
                .getBucketRequestRecordSinceId(
                    tracker.getLastProcessedId() == null ? 0 : tracker.getLastProcessedId());
        if (totalRecords != null && !totalRecords.isEmpty()) {
            log.debug("total data needs ETL is {}", totalRecords.size());
            Set<RawAccessRecord> foundedRecords =
                totalRecords.stream().filter(e -> !e.endpointNotFound())
                    .collect(Collectors.toSet());
            Set<RawAccessRecord> notFoundedRecords =
                totalRecords.stream().filter(RawAccessRecord::endpointNotFound)
                    .collect(Collectors.toSet());
            if (!notFoundedRecords.isEmpty()) {
                log.error("etl job summary, records with no endpoint id count {}, this is an issue",
                    notFoundedRecords.size());
            }
            Set<String> collect =
                foundedRecords.stream().map(RawAccessRecord::getUuid).collect(Collectors.toSet());
            Set<RawAccessRecord> responses = DomainRegistry.getRawAccessRecordRepository()
                .getResponseForUuid(collect);
            List<FormattedAccessRecord> records = new ArrayList<>();
            HashSet<String> issueIds = new HashSet<>();
            foundedRecords.forEach(request -> responses.stream()
                .filter(ee -> ee.getUuid().equals(request.getUuid()))
                .findFirst()
                .ifPresentOrElse(
                    response -> {
                        records.add(new FormattedAccessRecord(request, response));
                        request.markAsProcessed();
                        response.markAsProcessed();
                    },
                    () -> {
                        issueIds.add(request.getUuid());
                        log.error("unable to find response for uuid {}", request.getUuid());
                    }));
            if (!issueIds.isEmpty()) {
                context
                    .append(new RawAccessRecordProcessingWarning(issueIds));
            }
            Optional<Long> reduce =
                foundedRecords.stream().map(RawAccessRecord::getId).reduce(Math::max);
            reduce.ifPresent(tracker::setLastProcessedId);
            log.debug("etl job summary, next tracking id {}", tracker.getLastProcessedId());
            if (newTracker) {
                DomainRegistry.getDataProcessTrackerRepository().add(tracker);
            } else {
                DomainRegistry.getDataProcessTrackerRepository().update(tracker);
            }
            DomainRegistry.getFormattedAccessRecordRepository().addAll(records);
            DomainRegistry.getRawAccessRecordRepository().updateAllToProcessed(foundedRecords);
            log.debug("etl job summary, new records to insert count {}", records.size());
        } else {
            log.debug("no data found, ending this ETL job");
        }
    }
}
