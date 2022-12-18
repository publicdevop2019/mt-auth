package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.report.RawAccessRecord;
import com.mt.access.domain.model.report.RawAccessRecordRepository;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataJpaRawAccessRecordRepository
    extends JpaRepository<RawAccessRecord, Long>, RawAccessRecordRepository {

    default void add(RawAccessRecord rawAccessRecord) {
        save(rawAccessRecord);
    }

    default Set<RawAccessRecord> getBucketRequestRecordSinceId(Long id) {
        return findTop100ByIdGreaterThanAndIsRequestTrueOrderById(id);
    }

    default Set<RawAccessRecord> getResponseForUuid(Set<String> collect) {
        return getResponseForUuid_(collect);
    }

    @Query("select rar from RawAccessRecord rar where rar.uuid in ?1 and rar.isResponse is true")
    Set<RawAccessRecord> getResponseForUuid_(Set<String> collect);

    Set<RawAccessRecord> findTop100ByIdGreaterThanAndIsRequestTrueOrderById(Long id);


}
