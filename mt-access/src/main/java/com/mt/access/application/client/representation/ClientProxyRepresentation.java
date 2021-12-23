package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectURL;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.util.Set;
import java.util.stream.Collectors;

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
