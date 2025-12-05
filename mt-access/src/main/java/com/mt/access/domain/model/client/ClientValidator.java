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
        encryptedSecret();
    }


    private void encryptedSecret() {
        if (client.getSecret() == null) {
            if (!ClientType.FRONTEND_APP.equals(client.getType())) {
                handler.handleError("client secret required");
            }
        }
    }
}
