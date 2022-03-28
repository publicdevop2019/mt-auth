package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domainId.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ClientResourceCleanUpCompleted extends DomainEvent {

    public static final String CLIENT_RESOURCE_CLEAN_UP_COMPLETED =
        "client_resource_clean_up_completed";
    public static final String name = "CLIENT_RESOURCE_CLEAN_UP_COMPLETED";

    public ClientResourceCleanUpCompleted(Set<ClientId> pendingRevoked) {
        super(pendingRevoked.stream().map(e -> (DomainId) e).collect(Collectors.toSet()));
        setTopic(CLIENT_RESOURCE_CLEAN_UP_COMPLETED);
        setName(name);
    }
}
