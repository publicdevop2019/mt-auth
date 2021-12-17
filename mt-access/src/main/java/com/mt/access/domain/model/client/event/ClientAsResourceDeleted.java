package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientAsResourceDeleted extends DomainEvent {

    public static final String CLIENT_AS_RESOURCE_DELETED = "client_as_resource_deleted";
    public static final String name = "CLIENT_AS_RESOURCE_DELETED";
    public ClientAsResourceDeleted(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_AS_RESOURCE_DELETED);
        setName(name);
    }
}