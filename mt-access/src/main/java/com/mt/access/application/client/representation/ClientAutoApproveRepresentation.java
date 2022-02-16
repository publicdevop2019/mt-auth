package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import lombok.Data;

@Data
public class ClientAutoApproveRepresentation {
    private String id;
    private boolean autoApprove;

    public ClientAutoApproveRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        autoApprove = client.getAutoApprove();
    }
}
