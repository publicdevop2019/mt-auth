package com.mt.access.domain.model.report;

import static com.mt.access.infrastructure.AppConstant.ACCESS_DATA_PROCESSING_JOB_NAME;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.job.JobDetail;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RawAccessRecordProcessService {



    public void process() {
        DataProcessTracker tracker = DomainRegistry.getDataProcessTrackerRepository().getTracker();
        log.debug("last process id is {}", tracker.getLastProcessedId());
        Set<RawAccessRecord> totalRecords =
            DomainRegistry.getRawAccessRecordRepository()
                .getBucketRequestRecordSinceId(tracker.getLastProcessedId());
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
            foundedRecords.forEach(request -> responses.stream()
                .filter(ee -> ee.getUuid().equals(request.getUuid()))
                .findFirst()
                .ifPresentOrElse(
                    response -> {
                        records.add(new FormattedAccessRecord(request, response));
                        request.markAsProcessed();
                        response.markAsProcessed();
                    },
                    () -> log.error("unable to find response for uuid {}", request.getUuid())));
            DomainRegistry.getDataProcessTrackerRepository().updateTracker(tracker, foundedRecords);
            DomainRegistry.getFormattedAccessRecordRepository().saveBatch(records);
            log.debug("etl job summary, new records to insert count {}", records.size());
        } else {
            log.debug("no data found, ending this ETL job");
        }
    }
}
