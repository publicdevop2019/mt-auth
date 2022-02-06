package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientProxyRepresentation {

    protected String id;

    protected String name;

    protected String basePath;

    public ClientProxyRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        basePath = client.getPath();
    }
}
