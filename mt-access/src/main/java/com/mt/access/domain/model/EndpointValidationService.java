package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.system_role.RoleType;
import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.domain.model.system_role.SystemRoleQuery;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EndpointValidationService {
    public void validate(Endpoint endpoint, ValidationNotificationHandler handler) {
        hasValidClient(endpoint, handler);
        hasValidRoleGroup(endpoint, handler);
        hasValidCacheProfileId(endpoint, handler);
        websocketCanOnlyAccessByUser(endpoint, handler);
    }

    private void hasValidCacheProfileId(Endpoint endpoint, ValidationNotificationHandler handler) {
        if (endpoint.getCacheProfileId() != null) {
            Optional<CacheProfile> cacheProfile = DomainRegistry.getCacheProfileRepository().cacheProfileOfId(endpoint.getCacheProfileId());
            if (cacheProfile.isEmpty()) {
                handler.handleError("unable to find cache profile with id: " + endpoint.getCacheProfileId().getDomainId());
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

    private void websocketCanOnlyAccessByUser(Endpoint endpoint, ValidationNotificationHandler handler) {
        if (endpoint.isWebsocket()) {
            Optional<SystemRole> first = DomainRegistry.getSystemRoleRepository().systemRoleOfQuery(new SystemRoleQuery(endpoint.getSystemRoleId())).findFirst();
            first.ifPresent(e -> {
                if (!e.getRoleType().equals(RoleType.USER)) {
                    handler.handleError("websocket can only be access by user");
                }
            });
        }
    }

    private void hasValidRoleGroup(Endpoint endpoint, ValidationNotificationHandler handler) {
        if (endpoint.getSystemRoleId() != null) {
            Optional<SystemRole> first = DomainRegistry.getSystemRoleRepository().systemRoleOfQuery(new SystemRoleQuery(endpoint.getSystemRoleId())).findFirst();
            if (first.isEmpty()) {
                handler.handleError("unable to find role group with id: " + endpoint.getSystemRoleId().getDomainId());
            }
        }
    }
}
