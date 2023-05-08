package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import org.springframework.stereotype.Service;

@Service
public class EndpointValidationService {
    public void validate(Endpoint endpoint, ValidationNotificationHandler handler) {
        hasValidClient(endpoint);
        hasValidCacheProfileId(endpoint);
        sharedClientMustBeAccessible(endpoint, handler);
    }

    private void sharedClientMustBeAccessible(Endpoint endpoint,
                                              ValidationNotificationHandler handler) {
        if (endpoint.isShared()) {
            ClientId clientId = endpoint.getClientId();
            Client client = DomainRegistry.getClientRepository().by(clientId);
            if (!client.isAccessible()) {
                handler.handleError("shared endpoint client must be accessible: "
                    +
                    endpoint.getClientId().getDomainId());
            }
        }

    }

    private void hasValidCacheProfileId(Endpoint endpoint) {
        if (endpoint.getCacheProfileId() != null) {
            DomainRegistry.getCacheProfileRepository()
                .by(endpoint.getCacheProfileId());
        }
    }


    private void hasValidClient(Endpoint endpoint) {
        ClientId clientId = endpoint.getClientId();
        DomainRegistry.getClientRepository().by(clientId);
    }
}
