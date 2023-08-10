package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.report.RawAccessRecord;
import com.mt.access.domain.model.report.RawAccessRecordRepository;
import com.mt.common.domain.CommonDomainRegistry;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaRawAccessRecordRepository
    extends JpaRepository<RawAccessRecord, Long>, RawAccessRecordRepository {

    default Set<RawAccessRecord> getBucketRequestRecordSinceId(Long id) {
        return findTop100ByIdGreaterThanAndIsRequestTrueOrderById(id);
    }

    default Set<RawAccessRecord> getResponseForUuid(Set<String> collect) {
        return getResponseForUuid_(collect);
    }

    default void addAll(Set<RawAccessRecord> records) {
        List<RawAccessRecord> arrayList = new ArrayList<>(records);
        CommonDomainRegistry.getJdbcTemplate()
            .batchUpdate("INSERT INTO raw_access_record " +
                    "(" +
                    "id, " +
                    "name, " +
                    "instance_id, " +
                    "record_id, " +
                    "record, " +
                    "is_request, " +
                    "processed, " +
                    "is_response, " +
                    "uuid" +
                    ") VALUES " +
                    "(?,?,?,?,?,?,?,?,?)", arrayList, records.size(),
                (ps, record) -> {
                    ps.setLong(1, record.getId());
                    ps.setString(2, record.getName());
                    ps.setString(3, record.getInstanceId());
                    ps.setString(4, record.getRecordId());
                    ps.setString(5, record.getRecord());
                    ps.setBoolean(6, record.getIsRequest());
                    ps.setBoolean(7, record.getProcessed());
                    ps.setBoolean(8, record.getIsResponse());
                    ps.setString(9, record.getUuid());
                });
    }

    @Query("select rar from RawAccessRecord rar where rar.uuid in ?1 and rar.isResponse is true")
    Set<RawAccessRecord> getResponseForUuid_(Set<String> collect);

    Set<RawAccessRecord> findTop100ByIdGreaterThanAndIsRequestTrueOrderById(Long id);


}
