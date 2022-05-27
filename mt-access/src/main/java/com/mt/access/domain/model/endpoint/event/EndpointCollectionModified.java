package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.common.domain.model.domain_event.DomainEvent;

@AuditEvent
public class EndpointCollectionModified extends DomainEvent {

    public static final String ENDPOINT_COLLECTION_MODIFIED = "endpoint_collection_modified";
    public static final String name = "ENDPOINT_COLLECTION_MODIFIED";

    public EndpointCollectionModified() {
        super();
        setInternal(false);
        setTopic(ENDPOINT_COLLECTION_MODIFIED);
        setName(name);
    }
}
