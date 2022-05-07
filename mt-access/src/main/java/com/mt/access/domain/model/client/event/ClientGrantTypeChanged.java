package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientGrantTypeChanged extends DomainEvent implements AuditEvent {

    public static final String CLIENT_GRANT_TYPE_CHANGED = "client_grant_type_changed";
    public static final String name = "CLIENT_GRANT_TYPE_CHANGED";

    public ClientGrantTypeChanged(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_GRANT_TYPE_CHANGED);
        setName(name);
    }
}