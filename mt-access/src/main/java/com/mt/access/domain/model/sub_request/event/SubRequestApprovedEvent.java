package com.mt.access.domain.model.sub_request.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.sub_request.SubRequestId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AuditEvent
public class SubRequestApprovedEvent extends DomainEvent {
    public static final String SUB_REQ_APPROVED = "sub_req_approved";
    public static final String name = "SUB_REQ_APPROVED";

    {
        setTopic(SUB_REQ_APPROVED);
        setName(name);
    }

    public SubRequestApprovedEvent(SubRequestId endpointId) {
        super(endpointId);
    }
}
