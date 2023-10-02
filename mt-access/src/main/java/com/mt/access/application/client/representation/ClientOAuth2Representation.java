package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Setter
public class ClientOAuth2Representation {
    private ClientId clientId;
    @Getter
    private ProjectId projectId;
    @Getter
    private RoleId roleId;
    @Getter
    private String clientSecret;
    private Set<GrantType> grantTypes;
    private Integer accessTokenValiditySeconds;
    private Set<String> registeredRedirectUri;
    private Integer refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private Boolean autoApprove;

    public ClientOAuth2Representation(Client client) {
        setClientId(client.getClientId());
        setProjectId(client.getProjectId());
        setRoleId(client.getRoleId());
        setClientSecret(client.getSecret());
        setGrantTypes(client.getGrantTypes());
        setAccessTokenValiditySeconds(client.accessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds());
        Set<String> collect =
            client.getResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        Set<String> collect2 = client.getExternalResources().stream().map(ClientId::getDomainId)
            .collect(Collectors.toSet());
        collect2.addAll(collect);
        setResourceIds(collect2);
        setAutoApprove(client.getAutoApprove());
        setRegisteredRedirectUri(client.getRegisteredRedirectUri());
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

    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }


    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }


}
