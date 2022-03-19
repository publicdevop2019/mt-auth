package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ClientValidationService {
    public void validate(Client client, ValidationNotificationHandler handler) {
        validateResource(client, handler);
        validateExternalResource(client, handler);
    }

    private void validateResource(Client client, ValidationNotificationHandler handler) {
        if (!client.getResources().isEmpty()) {
            Set<Client> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) query), new ClientQuery(client.getResources()));
            if (allByQuery.size() != client.getResources().size()) {
                handler.handleError("unable to find all resource(s)");
            }
            boolean b = allByQuery.stream().anyMatch(e -> !e.isAccessible());
            if (b) {
                handler.handleError("resource(s) not accessible");
            }
        }
    }
    private void validateExternalResource(Client client, ValidationNotificationHandler handler) {
        if (!client.getExternalResources().isEmpty()) {
            Set<Client> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) query), new ClientQuery(client.getExternalResources()));
            if (allByQuery.size() != client.getExternalResources().size()) {
                handler.handleError("unable to find all external resource(s)");
            }
            boolean b = allByQuery.stream().anyMatch(e -> !e.isAccessible());
            if (b) {
                handler.handleError("resource(s) not accessible");
            }
        }
    }
}
