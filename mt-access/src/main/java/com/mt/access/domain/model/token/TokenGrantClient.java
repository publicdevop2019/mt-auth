package com.mt.access.domain.model.token;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.domain.model.validate.Utility;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Setter
public class TokenGrantClient {
    private ClientId clientId;
    @Getter
    private ProjectId projectId;
    @Getter
    private RoleId roleId;
    @Getter
    private Set<GrantType> grantTypes;
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private Set<String> registeredRedirectUri;

    public TokenGrantClient(Client client, Set<ClientId> resources) {
        setClientId(client.getClientId());
        setProjectId(client.getProjectId());
        setRoleId(client.getRoleId());
        setGrantTypes(client.getGrantTypes());
        setAccessTokenValiditySeconds(client.accessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        Set<String> collect = Utility.mapToSet(resources, ClientId::getDomainId);
        Set<String> collect2 =
            Utility.mapToSet(client.getExternalResources(), ClientId::getDomainId);
        collect2.addAll(collect);
        setResourceIds(collect2);
        setRegisteredRedirectUri(client.getRegisteredRedirectUri());
    }

    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    public String getClientId() {
        return clientId.getDomainId();
    }

    public Set<String> getResourceIds() {
        return resourceIds;
    }

    public Set<String> getAuthorizedGrantTypes() {
        return grantTypes.stream().map(e -> e.name().toLowerCase()).collect(Collectors.toSet());
    }

    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }


}
