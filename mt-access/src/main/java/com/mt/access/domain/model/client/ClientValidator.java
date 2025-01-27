package com.mt.access.domain.model.client;


import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class ClientValidator {
    private final Client client;
    private final ValidationNotificationHandler handler;

    public ClientValidator(Client client, ValidationNotificationHandler handler) {
        this.client = client;
        this.handler = handler;
    }

    protected void validate() {
        accessAndType();
        encryptedSecret();
        pathAndType();
        externalUrlAndType();
    }


    private void pathAndType() {
        if (Utility.isBlank(client.getPath())
            &&
            ClientType.BACKEND_APP.equals(client.getType())) {
            handler.handleError("backend client require path");
        }
        if (!Utility.isBlank(client.getPath())
            &&
            ClientType.FRONTEND_APP.equals(client.getType())) {
            handler.handleError("frontend client should not have path");
        }
    }

    private void externalUrlAndType() {
        if (client.getExternalUrl() == null
            &&
            ClientType.BACKEND_APP.equals(client.getType())) {
            handler.handleError("backend client require external url");
        }
        if (client.getExternalUrl() != null
            &&
            ClientType.FRONTEND_APP.equals(client.getType())) {
            handler.handleError("frontend client should not external url");
        }
    }

    private void encryptedSecret() {
        if (client.getSecret() == null) {
            if (!ClientType.FRONTEND_APP.equals(client.getType())) {
                handler.handleError("client secret required");
            }
        }
    }

    private void accessAndType() {
        if (client.getAccessible() != null && client.getAccessible()) {
            if (
                !ClientType.BACKEND_APP.equals(client.getType())
            ) {
                handler.handleError(
                    "invalid client type to be a resource, "
                        +
                        "must be backend application");
            }
        }
        if (!ClientType.BACKEND_APP.equals(client.getType())) {
            if (client.getAccessible() != null) {
                handler.handleError(
                    "only backend can specify accessible"
                );
            }
        }
        if (ClientType.BACKEND_APP.equals(client.getType())) {
            if (client.getAccessible() == null) {
                handler.handleError(
                    "backend must specify accessible"
                );
            }
        }
    }
}
