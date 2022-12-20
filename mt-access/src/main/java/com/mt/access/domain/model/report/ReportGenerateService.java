package com.mt.access.domain.model.report;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import java.time.Instant;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ReportGenerateService {
    public EndpointReport generatePast15MinutesReport(EndpointId endpointId) {
        return generateReport(endpointId, 15 * 60 * 1000);
    }

    public EndpointReport generatePast1HourReport(EndpointId endpointId) {
        return generateReport(endpointId, 60 * 60 * 1000);
    }

    public EndpointReport generatePast1DayReport(EndpointId endpointId) {
        return generateReport(endpointId, 24 * 60 * 60 * 1000);
    }

    public EndpointReport generateAllTimeReport(EndpointId endpointId) {
        Set<FormattedAccessRecord> endpointRecordWithinRange =
            DomainRegistry.getFormattedAccessRecordRepository()
                .getAllRecord(endpointId);
        return new EndpointReport(endpointRecordWithinRange, endpointId);
    }

    public EndpointReport generateReport(EndpointId endpointId, long offset) {
        long current = Instant.now().toEpochMilli();
        long past = current - offset;
        Set<FormattedAccessRecord> endpointRecordWithinRange =
            DomainRegistry.getFormattedAccessRecordRepository()
                .getRecordSince(endpointId, past);
        return new EndpointReport(endpointRecordWithinRange, endpointId);
    }
}
