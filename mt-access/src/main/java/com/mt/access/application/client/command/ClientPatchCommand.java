package com.mt.access.application.client.command;

import com.mt.access.domain.model.client.*;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.stream.Collectors;
@Getter
@NoArgsConstructor
public class ClientPatchCommand {
    private String description;
    private String name;
    private String path;
    private boolean resourceIndicator;
    private Set<String> scopeEnums;
    private Set<GrantType> grantTypeEnums;
    private Set<String> grantedAuthorities;
    private int accessTokenValiditySeconds = 0;
    private Set<String> resourceIds;

    public ClientPatchCommand(Client bizClient) {
        this.description = bizClient.getDescription();
        this.name = bizClient.getName();
        this.path = bizClient.getPath();
        this.resourceIndicator = bizClient.isAccessible();
        this.scopeEnums = bizClient.getScopes().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        this.grantTypeEnums = bizClient.getGrantTypes();
        this.accessTokenValiditySeconds = bizClient.accessTokenValiditySeconds();
        this.resourceIds = bizClient.getResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        this.grantedAuthorities = bizClient.getRoles().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
    }

}
