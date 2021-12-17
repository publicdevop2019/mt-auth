package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientCreated extends DomainEvent {

    public static final String CLIENT_CREATED = "client_created";
    public static final String name = "CLIENT_CREATED";
    public ClientCreated(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_CREATED);
        setName(name);
    }
}
