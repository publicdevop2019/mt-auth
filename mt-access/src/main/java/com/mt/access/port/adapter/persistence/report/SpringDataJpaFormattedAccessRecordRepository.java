package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.report.FormattedAccessRecord;
import com.mt.access.domain.model.report.FormattedAccessRecordRepository;
import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataJpaFormattedAccessRecordRepository
    extends FormattedAccessRecordRepository,
    JpaRepository<FormattedAccessRecord, Long> {

    default Set<FormattedAccessRecord> getRecordSince(EndpointId endpointId, long from) {
        return getRecordSince_(endpointId, Date.from(Instant.ofEpochMilli(from)));
    }

    default Set<FormattedAccessRecord> getAllRecord(EndpointId endpointId) {
        return getAllRecord_(endpointId);
    }

    default void addAll(List<FormattedAccessRecord> records) {
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate("INSERT INTO formatted_access_record " +
                    "(" +
                    "id, " +
                    "endpoint_id, " +
                    "request_at, " +
                    "path, " +
                    "client_ip, " +
                    "user_id, " +
                    "project_id, " +
                    "method, " +
                    "response_at, " +
                    "response_code, " +
                    "response_content_size" +
                    ") VALUES " +
                    "(?,?,?,?,?,?,?,?,?,?,?)", records, records.size(),
                (ps, record) -> {
                    ps.setLong(1, record.getId());
                    ps.setString(2, record.getEndpointId().getDomainId());
                    ps.setLong(3, record.getRequestAt());
                    ps.setString(4, record.getPath());
                    ps.setString(5, record.getClientIp());
                    ps.setString(6,
                        record.getUserId() == null ? null : record.getUserId().getDomainId());
                    ps.setString(7,
                        record.getProjectId() == null ? null : record.getProjectId().getDomainId());
                    ps.setString(8, record.getMethod());
                    ps.setLong(9, record.getResponseAt());
                    ps.setInt(10, record.getResponseCode());
                    ps.setInt(11, record.getResponseContentSize());
                });
    }

    @Query("select far from FormattedAccessRecord far where far.endpointId = ?1")
    Set<FormattedAccessRecord> getAllRecord_(EndpointId endpointId);

    @Query("select far from FormattedAccessRecord far where far.endpointId = ?1 and far.requestAt >= ?2")
    Set<FormattedAccessRecord> getRecordSince_(
        EndpointId endpointId, Date from);


}
