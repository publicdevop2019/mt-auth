package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.common.domain.model.validate.Utility;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientMgmtCardRepresentation {

    private String id;

    private String name;

    private String description;

    private Set<GrantType> grantTypeEnums;

    private Set<ClientType> types;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<ResourceClientInfo> resources;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;

    private Integer version;

    public ClientMgmtCardRepresentation(Client client, Set<ClientId> resources) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        grantTypeEnums = new HashSet<>();//avoid lazy load
        grantTypeEnums.addAll(client.getGrantTypes());
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        description = client.getDescription();
        registeredRedirectUri = client.getRedirectUrls().stream()
            .map(RedirectUrl::getValue).collect(Collectors.toSet());
        if (client.getTokenDetail() != null) {
            refreshTokenValiditySeconds = client.getTokenDetail().getRefreshTokenValiditySeconds();
        }
        resourceIds = Utility.mapToSet(resources, ClientId::getDomainId);
        resourceIndicator = client.getAccessible();
        types = new HashSet<>();//avoid lazy load
        types.addAll(client.getTypes());
    }

    @Data
    public static class ResourceClientInfo {
        private String name;
        private String id;

        public ResourceClientInfo(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
}
