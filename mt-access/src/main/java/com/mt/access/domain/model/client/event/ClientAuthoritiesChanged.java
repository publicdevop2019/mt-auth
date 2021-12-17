package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientAuthoritiesChanged extends DomainEvent {

    public static final String CLIENT_AUTHORITIES_CHANGED = "client_authorities_changed";
    public static final String name = "CLIENT_AUTHORITIES_CHANGED";
    public ClientAuthoritiesChanged(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_AUTHORITIES_CHANGED);
        setName(name);
    }
}