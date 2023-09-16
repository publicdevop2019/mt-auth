package com.mt.access.port.adapter.persistence.audit;

import com.mt.access.domain.model.audit.AuditRecord;
import com.mt.access.domain.model.audit.AuditRecordRepository;
import com.mt.common.domain.CommonDomainRegistry;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAuditRecordRepository implements AuditRecordRepository {

    private static final String INSERT_SQL =
        "INSERT INTO audit_record (id, action_name, detail, action_at, action_by) VALUES(?, ?, ?, ?, ?)";

    @Override
    public void add(AuditRecord auditRecord) {
        CommonDomainRegistry.getJdbcTemplate()
            .update(INSERT_SQL,
                auditRecord.getId(),
                auditRecord.getActionName(),
                auditRecord.getDetail(),
                auditRecord.getActionAt(),
                auditRecord.getActionBy()
            );
    }
}
