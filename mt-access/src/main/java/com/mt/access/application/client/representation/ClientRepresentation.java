package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import java.util.HashSet;
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
    private String externalUrl;

    private Set<GrantType> grantTypeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;

    private Integer version;
    private String clientSecret;

    private Boolean hasSecret;

    public ClientRepresentation(Client client) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        path = client.getPath();
        description = client.getDescription();
        grantTypeEnums = new HashSet<>();//avoid lazy load
        grantTypeEnums.addAll(client.getGrantTypes());
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        if (client.getRedirectDetail() != null) {
            registeredRedirectUri = client.getRedirectDetail().getRedirectUrls().stream()
                .map(RedirectUrl::getValue).collect(Collectors.toSet());
        }
        refreshTokenValiditySeconds = client.refreshTokenValiditySeconds();
        if (!ObjectUtils.isEmpty(client.getResources())) {
            resourceIds = client.getResources().stream().map(ClientId::getDomainId)
                .collect(Collectors.toSet());
        }
        resourceIndicator = client.getAccessible();
        if (client.getRedirectDetail() != null) {
            autoApprove = client.getRedirectDetail().getAutoApprove();
        }
        if (client.getExternalUrl() != null) {
            externalUrl = client.getExternalUrl().getValue();
        }
        version = client.getVersion();
        clientSecret = "masked";
        hasSecret = true;
        types = new HashSet<>();//avoid lazy load
        types.addAll(client.getTypes());

    }
}
