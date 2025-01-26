package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientCardRepresentation {

    private String id;

    private String name;

    private ClientType type;

    public ClientCardRepresentation(Client client1) {
        id = client1.getClientId().getDomainId();
        name = client1.getName();
        type = client1.getTypes().stream().findFirst().get();
    }
}
