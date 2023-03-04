package com.mt.access.domain.model.report.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.common.domain.model.domain_event.AnyDomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class RawAccessRecordProcessingWarning extends DomainEvent {
    public static final String RAW_ACCESS_RECORD_PROCESSING_WARNING =
        "raw_access_record_processing_warning";
    public static final String name = "RAW_ACCESS_RECORD_PROCESSING_WARNING";
    @Getter
    private Set<String> issueIds;

    {
        setTopic(RAW_ACCESS_RECORD_PROCESSING_WARNING);
        setName(name);

    }

    public RawAccessRecordProcessingWarning(Set<String> issueIds) {
        super(new AnyDomainId());
        this.issueIds = issueIds;
    }
}
