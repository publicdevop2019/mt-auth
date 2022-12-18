package com.mt.access.domain.model.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import java.util.List;
import java.util.Set;

public interface FormattedAccessRecordRepository {
    Set<FormattedAccessRecord> getRecordSince(EndpointId endpointId, long from);

    Set<FormattedAccessRecord> getAllRecord(EndpointId endpointId);

    void saveBatch(List<FormattedAccessRecord> formattedAccessRecordList);
}
