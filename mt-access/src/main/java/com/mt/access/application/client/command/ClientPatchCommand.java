package com.mt.access.application.client.command;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ClientPatchCommand {
    private String description;
    private String name;
    private String path;
    private Boolean resourceIndicator;
    private Set<GrantType> grantTypeEnums;
    private Integer accessTokenValiditySeconds;
    private Set<String> resourceIds;

    public ClientPatchCommand(Client client) {
        this.description = client.getDescription();
        this.name = client.getName();
        this.path = client.getPath();
        this.resourceIndicator = client.isAccessible();
        this.grantTypeEnums = client.getGrantTypes();
        this.accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        this.resourceIds = client.getResources().stream().map(ClientId::getDomainId)
            .collect(Collectors.toSet());
    }

}
