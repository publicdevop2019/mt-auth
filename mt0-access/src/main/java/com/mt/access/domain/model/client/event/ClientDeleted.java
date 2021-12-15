package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientDeleted extends DomainEvent {

    public static final String CLIENT_DELETED = "client_deleted";
    public static final String name = "CLIENT_DELETED";
    public ClientDeleted(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_DELETED);
        setName(name);
    }
}