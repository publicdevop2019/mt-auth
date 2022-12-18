package com.mt.access.domain.model.report;

import com.mt.access.domain.DomainRegistry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RawAccessRecordEtlService {
    public void processData() {
        DataProcessTracker tracker = DomainRegistry.getDataProcessTrackerRepository().getTracker();
        log.debug("last process id is {}", tracker.getLastProcessedId());
        Set<RawAccessRecord> requests =
            DomainRegistry.getRawAccessRecordRepository()
                .getBucketRequestRecordSinceId(tracker.getLastProcessedId());
        if (requests != null && !requests.isEmpty()) {
            log.debug("total data needs ETL is {}", requests.size());
            Set<String> collect =
                requests.stream().map(RawAccessRecord::getUuid).collect(Collectors.toSet());
            Set<RawAccessRecord> responses = DomainRegistry.getRawAccessRecordRepository()
                .getResponseForUuid(collect);
            List<FormattedAccessRecord> records = new ArrayList<>();
            requests.forEach(request -> responses.stream()
                .filter(ee -> ee.getUuid().equals(request.getUuid()))
                .findFirst()
                .ifPresentOrElse(
                    response -> {
                        records.add(new FormattedAccessRecord(request, response));
                        request.markAsProcessed();
                        response.markAsProcessed();
                    },
                    () -> log.error("unable to find response for uuid {}", request.getUuid())));
            DomainRegistry.getDataProcessTrackerRepository().updateTracker(tracker, requests);
            DomainRegistry.getFormattedAccessRecordRepository().saveBatch(records);
        } else {
            log.debug("no data found, ending this ETL job");
        }
    }
}
