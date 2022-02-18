package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.HtmlUtils;

import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ClientCardRepresentation {

    protected String id;

    protected String name;

    protected String description;

    protected Set<GrantType> grantTypeEnums;

    protected Set<ClientType> types;

    protected int accessTokenValiditySeconds;

    protected Set<String> registeredRedirectUri;

    protected int refreshTokenValiditySeconds;

    protected Set<String> resourceIds;

    protected boolean resourceIndicator;

    protected boolean autoApprove;

    protected int version;

    public ClientCardRepresentation(Client client1) {
        id = client1.getClientId().getDomainId();
        name = client1.getName();
        grantTypeEnums = client1.getGrantTypes();
        accessTokenValiditySeconds = client1.accessTokenValiditySeconds();
        description = client1.getDescription();
        if (client1.getAuthorizationCodeGrant() != null)
            registeredRedirectUri = client1.getAuthorizationCodeGrant().getRedirectUrls().stream().map(RedirectURL::getValue).collect(Collectors.toSet());
        refreshTokenValiditySeconds = client1.getTokenDetail().getRefreshTokenValiditySeconds();
        if (!ObjectUtils.isEmpty(client1.getResources()))
            resourceIds = client1.getResources().stream().map(ClientId::getDomainId).collect(Collectors.toSet());
        resourceIndicator = client1.isAccessible();
        types=client1.getTypes();
    }
}
