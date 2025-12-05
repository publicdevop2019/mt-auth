package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.common.infrastructure.Utility;
import java.util.Set;
import lombok.Data;

@Data
public class ClientRepresentation {
    private ClientType type;
    private String id;

    private String name;
    private String projectId;

    private String description;

    private Set<GrantType> grantTypeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Integer version;
    private String clientSecret;

    private Boolean hasSecret;

    public ClientRepresentation(Client client, Set<RedirectUrl> urls,
                                Set<GrantType> grantTypes) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        description = client.getDescription();
        grantTypeEnums = grantTypes;
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        registeredRedirectUri = Utility.mapToSet(urls, RedirectUrl::getValue);
        refreshTokenValiditySeconds = client.refreshTokenValiditySeconds();
        version = client.getVersion();
        clientSecret = client.getSecret();
        projectId = client.getProjectId().getDomainId();
        type = client.getType();
    }
}
