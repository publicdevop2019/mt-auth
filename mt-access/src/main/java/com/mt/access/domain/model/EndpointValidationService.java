package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class EndpointValidationService {
    public void validate(Endpoint endpoint, ValidationNotificationHandler handler) {
        hasValidClient(endpoint, handler);
        hasValidCacheProfileId(endpoint, handler);
        sharedClientMustBeAccessible(endpoint, handler);
    }

    private void sharedClientMustBeAccessible(Endpoint endpoint,
                                              ValidationNotificationHandler handler) {
        if (endpoint.isShared()) {
            ClientId clientId = endpoint.getClientId();
            Optional<Client> client = DomainRegistry.getClientRepository().clientOfId(clientId);
            if (client.isEmpty()) {
                handler.handleError(
                    "endpoint client not found: " + endpoint.getClientId().getDomainId());
            } else {
                if (!client.get().isAccessible()) {
                    handler.handleError("shared endpoint client must be accessible: "
                        +
                        endpoint.getClientId().getDomainId());
                }
            }
        }

    }

    private void hasValidCacheProfileId(Endpoint endpoint, ValidationNotificationHandler handler) {
        if (endpoint.getCacheProfileId() != null) {
            Optional<CacheProfile> cacheProfile = DomainRegistry.getCacheProfileRepository()
                .cacheProfileOfId(endpoint.getCacheProfileId());
            if (cacheProfile.isEmpty()) {
                handler.handleError("unable to find cache profile with id: "
                    +
                    endpoint.getCacheProfileId().getDomainId());
            }
        }
    }


    private void hasValidClient(Endpoint endpoint, ValidationNotificationHandler handler) {
        ClientId clientId = endpoint.getClientId();
        Optional<Client> client = DomainRegistry.getClientRepository().clientOfId(clientId);
        if (client.isEmpty()) {
            handler.handleError("can not update endpoint it which clientId is deleted or unknown");
        }
    }
}
