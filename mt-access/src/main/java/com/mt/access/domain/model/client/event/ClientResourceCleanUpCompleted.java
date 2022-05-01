package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.HashSet;
import java.util.Set;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientResourceCleanUpCompleted extends DomainEvent {

    public static final String CLIENT_RESOURCE_CLEAN_UP_COMPLETED =
        "client_resource_clean_up_completed";
    public static final String name = "CLIENT_RESOURCE_CLEAN_UP_COMPLETED";

    public ClientResourceCleanUpCompleted(Set<ClientId> pendingRevoked) {
        super(new HashSet<>(pendingRevoked));
        setTopic(CLIENT_RESOURCE_CLEAN_UP_COMPLETED);
        setName(name);
    }
}
