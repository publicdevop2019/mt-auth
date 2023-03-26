package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientAccessibilityRemoved extends DomainEvent {

    public static final String CLIENT_ACCESSIBILITY_REMOVED = "client_accessibility_removed";
    public static final String name = "CLIENT_ACCESSIBILITY_REMOVED";

    {
        setTopic(CLIENT_ACCESSIBILITY_REMOVED);
        setName(name);

    }

    public ClientAccessibilityRemoved(ClientId clientId) {
        super(clientId);
    }
}
