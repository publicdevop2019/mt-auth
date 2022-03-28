package com.mt.access.domain.model.client;


import com.mt.common.domain.model.validate.ValidationNotificationHandler;

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
        typeAndGrantType();
    }

    private void tokenAndGrantType() {
        if (client.getGrantTypes() != null && !client.getGrantTypes().isEmpty()) {
            if (client.getTokenDetail().getAccessTokenValiditySeconds() == null
                ||
                client.getTokenDetail().getAccessTokenValiditySeconds() < 60) {
                handler
                    .handleError("when grant present access token validity seconds must be valid");
            }
            if (client.getGrantTypes().contains(GrantType.REFRESH_TOKEN)) {
                if (client.getTokenDetail().getRefreshTokenValiditySeconds() == null
                    ||
                    client.getTokenDetail().getRefreshTokenValiditySeconds() < 120) {
                    handler
                        .handleError("refresh grant must has valid refresh token validity seconds");
                }
            }
        }
    }

    private void typeAndGrantType() {
        if (client.getGrantTypes().contains(GrantType.AUTHORIZATION_CODE)
            &&
            !client.getTypes().contains(ClientType.FRONTEND_APP)) {
            handler.handleError("only frontend client allows authorization code grant");
        }
    }

    private void encryptedSecret() {
        if (client.getSecret() == null) {
            if (client.getTypes().stream().noneMatch(e -> e.equals(ClientType.FRONTEND_APP))) {
                handler.handleError("client secret required");
            }
        }
    }

    private void accessAndRoles() {
        if (client.isAccessible()) {
            if (
                client.getTypes().stream().anyMatch(e -> e.equals(ClientType.THIRD_PARTY))
                    || client.getTypes().stream().anyMatch(e -> e.equals(ClientType.FRONTEND_APP))
            ) {
                handler.handleError(
                    "invalid client type to be a resource, "
                        +
                        "must be first party & backend application");
            }
        }
    }
}
