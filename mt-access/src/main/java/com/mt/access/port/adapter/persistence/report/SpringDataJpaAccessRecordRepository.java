package com.mt.access.port.adapter.persistence.report;

import com.mt.access.domain.model.report.AccessRecord;
import com.mt.access.domain.model.report.AccessRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaAccessRecordRepository extends JpaRepository<AccessRecord,Long>,
    AccessRecordRepository {
    default void add(AccessRecord accessRecord){
        save(accessRecord);
    };
}
