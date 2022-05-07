package com.mt.access.application.audit;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.application.domain_event.StoredEventRepresentation;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import org.springframework.stereotype.Service;

@Service
public class AuditApplicationService {
    public SumPagedRep<StoredEventRepresentation> auditEvents(String queryParam, String pageParam,
                                                              String skipCount) {
        SumPagedRep<StoredEvent> auditEvent =
            DomainRegistry.getAuditService().getAuditEvent(queryParam, pageParam, skipCount);
        return new SumPagedRep<>(auditEvent, StoredEventRepresentation::new);
    }
}
