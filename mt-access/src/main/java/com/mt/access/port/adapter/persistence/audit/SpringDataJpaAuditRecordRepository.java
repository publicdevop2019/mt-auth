package com.mt.access.port.adapter.persistence.audit;

import com.mt.access.domain.model.audit.AuditRecord;
import com.mt.access.domain.model.audit.AuditRecordRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataJpaAuditRecordRepository extends AuditRecordRepository,
    JpaRepository<AuditRecord, Long> {
    default void add(AuditRecord auditRecord) {
        save(auditRecord);
    }
}
