package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import org.springframework.stereotype.Service;

@Service
public class EndpointValidationService {

    private static void compareProjectId(ProjectId a, ProjectId b) {
        if (!Utility.equals(a, b)) {
            throw new DefinedRuntimeException("project id mismatch", "1010",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void validate(Endpoint endpoint, ValidationNotificationHandler handler) {
        hasValidClient(endpoint);
        hasValidCacheProfileId(endpoint);
        hasValidCorsProfileId(endpoint);
        sharedClientMustBeAccessible(endpoint, handler);
    }

    private void hasValidCorsProfileId(Endpoint endpoint) {
        if (endpoint.getCorsProfileId() != null) {
            CorsProfile corsProfile = DomainRegistry.getCorsProfileRepository()
                .get(endpoint.getCorsProfileId());
            compareProjectId(endpoint.getProjectId(), corsProfile.getProjectId());
        }
    }

    private void sharedClientMustBeAccessible(Endpoint endpoint,
                                              ValidationNotificationHandler handler) {
        if (endpoint.getShared()) {
            ClientId clientId = endpoint.getClientId();
            Client client = DomainRegistry.getClientRepository().get(clientId);
            if (!client.getAccessible()) {
                handler.handleError("shared endpoint client must be accessible: "
                    +
                    endpoint.getClientId().getDomainId());
            }
        }

    }

    private void hasValidCacheProfileId(Endpoint endpoint) {
        if (endpoint.getCacheProfileId() != null) {
            CacheProfile cacheProfile = DomainRegistry.getCacheProfileRepository()
                .get(endpoint.getCacheProfileId());
            compareProjectId(endpoint.getProjectId(), cacheProfile.getProjectId());
        }
    }

    private void hasValidClient(Endpoint endpoint) {
        Client client = DomainRegistry.getClientRepository().get(endpoint.getClientId());
        compareProjectId(endpoint.getProjectId(), client.getProjectId());
    }
}
