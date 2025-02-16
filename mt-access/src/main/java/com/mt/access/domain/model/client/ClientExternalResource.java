package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import com.mt.common.infrastructure.Utility;
import java.util.Set;

public class ClientExternalResource {

    public static void update(Client client,
                              Set<ClientId> oldResources,
                              Set<ClientId> newResources) {
        if (!Checker.sameAs(oldResources, newResources)) {
            Utility.updateSet(oldResources, newResources,
                (added) -> {
                    validate(added);
                    DomainRegistry.getClientExternalResourceRepository().add(client, added);
                },
                (removed) -> DomainRegistry.getClientExternalResourceRepository()
                    .remove(client, removed)
            );
        }
    }

    private static void validate(Set<ClientId> resources) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        if (Checker.notNull(resources) &&
            Checker.notEmpty(resources)) {
            Set<Client> allByQuery = QueryUtility.getAllByQuery(
                (query) -> DomainRegistry.getClientRepository().query(query),
                new ClientQuery(resources));
            if (allByQuery.size() != resources.size()) {
                handler.handleError("unable to find all external resource(s)");
            }
            boolean b = allByQuery.stream().anyMatch(e -> !e.getAccessible());
            if (b) {
                handler.handleError("resource(s) not accessible");
            }
        }
    }

}
