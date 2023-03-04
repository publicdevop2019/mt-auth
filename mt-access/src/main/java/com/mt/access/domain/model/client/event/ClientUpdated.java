package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientUpdated extends DomainEvent {

    public static final String CLIENT_UPDATED = "client_updated";
    public static final String name = "CLIENT_UPDATED";

    {
        setTopic(CLIENT_UPDATED);
        setName(name);

    }

    public ClientUpdated(ClientId clientId) {
        super(clientId);
    }
}