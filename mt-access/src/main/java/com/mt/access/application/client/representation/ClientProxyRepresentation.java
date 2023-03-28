package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientProxyRepresentation {

    private String id;

    private String name;

    private String basePath;

    private String projectId;

    private String externalUrl;

    public ClientProxyRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        basePath = client.getPath();
        projectId = client.getProjectId().getDomainId();
        if (client.getExternalUrl() != null) {
            externalUrl = client.getExternalUrl().getValue();
        }
    }
}
