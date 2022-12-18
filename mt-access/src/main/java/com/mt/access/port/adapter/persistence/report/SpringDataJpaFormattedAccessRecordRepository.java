package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.report.FormattedAccessRecord;
import com.mt.access.domain.model.report.FormattedAccessRecordRepository;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataJpaFormattedAccessRecordRepository
    extends FormattedAccessRecordRepository,
    JpaRepository<FormattedAccessRecord, Long> {

    default Set<FormattedAccessRecord> getRecordSince(EndpointId endpointId, long from) {
        return getRecordSince_(endpointId, from);
    }

    default Set<FormattedAccessRecord> getAllRecord(EndpointId endpointId) {
        return getAllRecord_(endpointId);
    }

    default void saveBatch(List<FormattedAccessRecord> formattedAccessRecordList) {
        saveAll(formattedAccessRecordList);
    }

    @Query("select far from FormattedAccessRecord far where far.endpointId = ?1")
    Set<FormattedAccessRecord> getAllRecord_(EndpointId endpointId);

    @Query("select far from FormattedAccessRecord far where far.endpointId = ?1 and far.requestAt >= ?2")
    Set<FormattedAccessRecord> getRecordSince_(
        EndpointId endpointId, long from);


}
