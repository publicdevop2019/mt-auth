package com.mt.access.domain.model.client;


import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.system_role.RoleType;
import com.mt.access.domain.model.system_role.SystemRole;
import com.mt.access.domain.model.system_role.SystemRoleQuery;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;

import java.util.Set;

public class ClientValidator {
    private final Client client;
    private final ValidationNotificationHandler handler;

    public ClientValidator(Client client, ValidationNotificationHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    protected void validate() {
        accessAndRoles();
        encryptedSecret();
        tokenAndGrantType();
        onlyRoleWithClientType();
        mustHaveScopeIfAuthorize();
    }

    private void mustHaveScopeIfAuthorize() {
        if(client.getGrantTypes().contains(GrantType.AUTHORIZATION_CODE)){
            Validator.notEmpty(client.getScopes());
        }
    }

    private void onlyRoleWithClientType() {
        Set<SystemRole> allByQuery = QueryUtility.getAllByQuery((query) -> DomainRegistry.getSystemRoleRepository().systemRoleOfQuery((SystemRoleQuery) query), new SystemRoleQuery(client.getRoles()));
        if(allByQuery.stream().anyMatch(e->e.getRoleType().equals(RoleType.USER))){
            handler.handleError("client can only has client roles");
        }
    }

    private void tokenAndGrantType() {
        if (client.getGrantTypes() != null && !client.getGrantTypes().isEmpty()) {
            if (client.getTokenDetail().getAccessTokenValiditySeconds() == null || client.getTokenDetail().getAccessTokenValiditySeconds() < 60)
                handler.handleError("when grant present access token validity seconds must be valid");
            if (client.getGrantTypes().contains(GrantType.REFRESH_TOKEN)) {
                if (client.getTokenDetail().getRefreshTokenValiditySeconds() == null || client.getTokenDetail().getRefreshTokenValiditySeconds() < 120)
                    handler.handleError("refresh grant must has valid refresh token validity seconds");
            }
        }
    }

    private void encryptedSecret() {
        if (client.getSecret() == null)
            handler.handleError("client secret required");
    }

    private void accessAndRoles() {
        if (client.isAccessible()) {
            if (
                    client.getRoles().stream().noneMatch(e -> e.getDomainId().equals(AppConstant.FIRST_PARTY_ID))
                            || client.getRoles().stream().noneMatch(e -> e.getDomainId().equals(AppConstant.BACKEND_ID))
            ) {
                handler.handleError("invalid grantedAuthorities to be a resource, must be first party & backend application");
            }
        }
    }
}
