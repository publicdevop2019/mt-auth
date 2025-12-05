package com.mt.access.domain.model.token;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.infrastructure.Utility;
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
    private Set<String> registeredRedirectUri;

    public TokenGrantClient(Client client, Set<RedirectUrl> redirectUrls,
                            Set<GrantType> grantTypes) {
        setClientId(client.getClientId());
        setProjectId(client.getProjectId());
        setRoleId(client.getRoleId());
        setGrantTypes(grantTypes);
        setAccessTokenValiditySeconds(client.accessTokenValiditySeconds());
        setRefreshTokenValiditySeconds(client.getRefreshTokenValiditySeconds(grantTypes));
        setRegisteredRedirectUri(Utility.mapToSet(redirectUrls, RedirectUrl::getValue));
    }

    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    public String getClientId() {
        return clientId.getDomainId();
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
