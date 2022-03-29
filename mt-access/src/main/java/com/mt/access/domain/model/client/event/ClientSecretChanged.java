package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientSecretChanged extends DomainEvent {

    public static final String CLIENT_SECRET_CHANGED = "client_secret_changed";
    public static final String name = "CLIENT_SECRET_CHANGED";

    public ClientSecretChanged(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_SECRET_CHANGED);
        setName(name);
    }
}