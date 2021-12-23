package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientPathChanged extends DomainEvent {

    public static final String CLIENT_PATH_CHANGED = "client_path_changed";
    public static final String name = "CLIENT_PATH_CHANGED";
    public ClientPathChanged(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_PATH_CHANGED);
        setName(name);
        setInternal(false);
    }
}