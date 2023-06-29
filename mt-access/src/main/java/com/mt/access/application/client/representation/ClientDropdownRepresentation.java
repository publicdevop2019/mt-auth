package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientDropdownRepresentation {

    private String id;

    private String name;

    public ClientDropdownRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        name = client.getName();
    }
}
