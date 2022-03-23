package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ClientRepository {

    Optional<Client> clientOfId(ClientId clientId);

    void add(Client client);

    void remove(Client client);

    SumPagedRep<Client> clientsOfQuery(ClientQuery clientQuery);

    void remove(Collection<Client> clients);

    Set<ProjectId> getProjectIds();

    Set<ClientId> allClientIds();
}
