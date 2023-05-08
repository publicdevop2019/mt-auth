package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collection;
import java.util.Set;

public interface ClientRepository {

    default Client by(ClientId clientId){
        Client client = byNullable(clientId);
        Validator.notNull(client);
        return client;
    }
    Client byNullable(ClientId clientId);

    void add(Client client);

    void remove(Client client);

    void remove(Collection<Client> clients);

    SumPagedRep<Client> query(ClientQuery clientQuery);

    Set<ProjectId> getProjectIds();

    Set<ClientId> allClientIds();

    long countTotal();

    long countProjectTotal(ProjectId projectId);
}
