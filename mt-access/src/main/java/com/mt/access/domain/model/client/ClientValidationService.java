package com.mt.access.domain.model.client;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ClientValidationService {
    public void validate(Client client, ValidationNotificationHandler handler) {
        validateExternalResource(client, handler);
    }

    private void validateExternalResource(Client client, ValidationNotificationHandler handler) {
        if (Utility.notNull(client.getExternalResources()) &&
            Utility.notEmpty(client.getExternalResources())) {
            Set<Client> allByQuery = QueryUtility.getAllByQuery(
                (query) -> DomainRegistry.getClientRepository().query(query),
                new ClientQuery(client.getExternalResources()));
            if (allByQuery.size() != client.getExternalResources().size()) {
                handler.handleError("unable to find all external resource(s)");
            }
            boolean b = allByQuery.stream().anyMatch(e -> !e.getAccessible());
            if (b) {
                handler.handleError("resource(s) not accessible");
            }
        }
    }
}
