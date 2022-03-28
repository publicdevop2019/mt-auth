package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.util.ObjectUtils;

@Data
public class ClientRepresentation {
    private final Set<ClientType> types;
    private String id;

    private String name;

    private String path;

    private String description;

    private Set<GrantType> grantTypeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private boolean resourceIndicator;

    private boolean autoApprove;

    private Integer version;
    private String clientSecret;

    private boolean hasSecret;

    public ClientRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        path = client.getPath();
        description = client.getDescription();
        grantTypeEnums = client.getGrantTypes();
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        if (client.getAuthorizationCodeGrant() != null) {
            registeredRedirectUri = client.getAuthorizationCodeGrant().getRedirectUrls().stream()
                .map(RedirectUrl::getValue).collect(Collectors.toSet());
        }
        refreshTokenValiditySeconds = client.refreshTokenValiditySeconds();
        if (!ObjectUtils.isEmpty(client.getResources())) {
            resourceIds = client.getResources().stream().map(ClientId::getDomainId)
                .collect(Collectors.toSet());
        }
        resourceIndicator = client.isAccessible();
        if (client.getAuthorizationCodeGrant() != null) {
            autoApprove = client.getAuthorizationCodeGrant().isAutoApprove();
        }
        version = client.getVersion();
        clientSecret = "masked";
        hasSecret = true;
        types = client.getTypes();

    }
}
