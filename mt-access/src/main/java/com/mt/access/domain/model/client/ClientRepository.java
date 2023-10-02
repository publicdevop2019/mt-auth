package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collection;
import java.util.Set;

public interface ClientRepository {

    default Client get(ClientId clientId) {
        Client client = query(clientId);
        Validator.notNull(client);
        return client;
    }

    default Client get(ProjectId projectId, ClientId clientId) {
        SumPagedRep<Client> query = query(new ClientQuery(clientId, projectId));
        Client client = query.findFirst().orElse(null);
        Validator.notNull(client);
        return client;
    }

    Client query(ClientId clientId);

    void add(Client client);

    void remove(Client client);

    void remove(Collection<Client> clients);

    SumPagedRep<Client> query(ClientQuery clientQuery);

    Set<ProjectId> getProjectIds();

    Set<ClientId> allClientIds();

    long countTotal();

    long countProjectTotal(ProjectId projectId);

    Set<ClientId> getResources(Long id);

    Set<ClientId> getExternalResources(Long id);

    Set<GrantType> getGrantType(Long id);

    Set<ClientType> getType(Long id);

    Set<RedirectUrl> getRedirectUrls(Long id);

    void update(Client old, Client update);

    void removeRef(ClientId removedClientId);
}
