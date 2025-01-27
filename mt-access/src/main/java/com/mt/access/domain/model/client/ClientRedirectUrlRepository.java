package com.mt.access.domain.model.client;

import java.util.Set;

public interface ClientRedirectUrlRepository {
    Set<RedirectUrl> query(Client client);

    void remove(Client client, Set<RedirectUrl> redirectUrls);

    void add(Client client, Set<RedirectUrl> redirectUrls);

    void removeAll(Client client, Set<RedirectUrl> redirectUrls);
}
