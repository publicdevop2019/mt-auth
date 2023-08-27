package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collection;
import java.util.Set;

public interface ClientRepository {
    /**
     * special method for login only,
     * eager load everything
     * @param clientId client
     * @return client used for login
     */
    LoginOAuthClient getForLogin(ClientId clientId);

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
}
