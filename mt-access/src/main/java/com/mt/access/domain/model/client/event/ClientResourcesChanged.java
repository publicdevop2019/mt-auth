package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientResourcesChanged extends DomainEvent {

    public static final String CLIENT_RESOURCES_CHANGED = "client_resources_changed";
    public static final String name = "CLIENT_RESOURCES_CHANGED";

    {

        setTopic(CLIENT_RESOURCES_CHANGED);
        setName(name);
    }

    public ClientResourcesChanged(ClientId clientId) {
        super(clientId);
    }
}