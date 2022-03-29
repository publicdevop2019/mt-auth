package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientTokenDetailChanged extends DomainEvent {

    public static final String CLIENT_TOKEN_DETAIL_CHANGED = "client_token_detail_changed";
    public static final String name = "CLIENT_TOKEN_DETAIL_CHANGED";

    public ClientTokenDetailChanged(ClientId clientId) {
        super(clientId);
        setTopic(CLIENT_TOKEN_DETAIL_CHANGED);
        setName(name);
    }
}