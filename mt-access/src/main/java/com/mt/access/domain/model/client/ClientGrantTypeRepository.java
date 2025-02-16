package com.mt.access.domain.model.client;

import java.util.Set;

public interface ClientGrantTypeRepository {
    Set<GrantType> query(Client client);

    void remove(Client client, Set<GrantType> grantTypes);

    void add(Client client, Set<GrantType> grantTypes);

    void removeAll(Client client, Set<GrantType> grantTypes);
}
