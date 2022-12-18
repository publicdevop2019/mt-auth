package com.mt.access.domain.model.report;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerateService {
    public EndpointReport generatePast15MinutesReport(EndpointId endpointId) {
        long current = Instant.now().getEpochSecond();
        long past = current - 15 * 60;
        Set<FormattedAccessRecord> endpointRecordWithinRange =
            DomainRegistry.getFormattedAccessRecordRepository()
                .getRecordSince(endpointId, past);
        return new EndpointReport(endpointRecordWithinRange,endpointId);
    }

    public EndpointReport generatePast1HourReport(EndpointId endpointId) {

        long current = Instant.now().getEpochSecond();
        long past = current - 60 * 60;
        Set<FormattedAccessRecord> endpointRecordWithinRange =
            DomainRegistry.getFormattedAccessRecordRepository()
                .getRecordSince(endpointId, past);
        return new EndpointReport(endpointRecordWithinRange,endpointId);
    }

    public EndpointReport generatePast1DayReport(EndpointId endpointId) {
        long current = Instant.now().getEpochSecond();
        long past = current - 24 * 60 * 60;
        Set<FormattedAccessRecord> endpointRecordWithinRange =
            DomainRegistry.getFormattedAccessRecordRepository()
                .getRecordSince(endpointId, past);
        return new EndpointReport(endpointRecordWithinRange,endpointId);
    }

    public EndpointReport generateAllTimeReport(EndpointId endpointId) {
        Set<FormattedAccessRecord> endpointRecordWithinRange =
            DomainRegistry.getFormattedAccessRecordRepository()
                .getAllRecord(endpointId);
        return new EndpointReport(endpointRecordWithinRange,endpointId);
    }
}
