package com.mt.access.domain.model.client;

import java.util.Set;

public interface ClientExternalResourceRepository {
    Set<ClientId> query(Client client);

    void remove(Client client, Set<ClientId> resources);

    void add(Client client, Set<ClientId> resources);

    void removeAll(Client client, Set<ClientId> resources);
}
