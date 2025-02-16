package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.event.ClientResourcesChanged;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.local_transaction.TransactionContext;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.common.infrastructure.Utility;
import java.util.Set;

public class ClientResource {

    public static void add(Client client, Set<ClientId> newResources) {
        if (Checker.notNullOrEmpty(newResources)) {
            Validator.lessThanOrEqualTo(newResources, 10);
            validate(client.getClientId(), client.getProjectId(), newResources);
            DomainRegistry.getClientResourceRepository().add(client, newResources);
        }
    }

    public static void update(Client client,
                              Set<ClientId> oldResources,
                              Set<ClientId> newResources,
                              TransactionContext context) {
        if (!Checker.sameAs(oldResources, newResources)) {
            Validator.lessThanOrEqualTo(newResources, 10);
            context.append(new ClientResourcesChanged(client.getClientId()));
            Utility.updateSet(oldResources, newResources,
                (added) -> {
                    validate(client.getClientId(), client.getProjectId(), added);
                    DomainRegistry.getClientResourceRepository().add(client, added);
                },
                (removed) -> DomainRegistry.getClientResourceRepository().remove(client, removed));
        }
    }

    private static void validate(ClientId clientId, ProjectId projectId,
                                 Set<ClientId> resources) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        if (resources.contains(clientId)) {
            handler.handleError("client cannot have itself as resource");
        }
        Set<Client> allByQuery = QueryUtility.getAllByQuery(
            (query) -> DomainRegistry.getClientRepository().query(query),
            new ClientQuery(resources));
        if (allByQuery.size() != resources.size()) {
            handler.handleError("unable to find all resource(s)");
        }
        boolean b = allByQuery.stream().anyMatch(e -> !e.getAccessible());
        if (b) {
            handler.handleError("resource(s) not accessible");
        }
        if (allByQuery.stream().map(Client::getProjectId)
            .anyMatch(e -> !projectId.equals(e))) {
            handler.handleError("client belongs to another project");
        }
    }

}
