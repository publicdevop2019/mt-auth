package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientScopesChanged extends DomainEvent {

    public static final String CLIENT_SCOPES_CHANGED = "client_scopes_changed";
    public static final String name = "CLIENT_SCOPES_CHANGED";
    public ClientScopesChanged(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_SCOPES_CHANGED);
        setName(name);
    }
}