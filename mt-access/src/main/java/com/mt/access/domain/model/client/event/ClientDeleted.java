package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class ClientDeleted extends DomainEvent {

    public static final String CLIENT_DELETED = "client_deleted";
    public static final String name = "CLIENT_DELETED";
    @Getter
    private String changeId;

    public ClientDeleted(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_DELETED);
        setName(name);
        this.changeId = clientId.getDomainId() + "_cancel";
    }
}