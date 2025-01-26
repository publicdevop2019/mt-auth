package com.mt.access.domain.model.client;

import java.util.Set;

public interface ClientResourceRepository {
    Set<ClientId> query(Client client);

    void add(Client client, Set<ClientId> resources);

    void remove(Client client, Set<ClientId> resources);

    void removeRef(ClientId removedClientId);
}
