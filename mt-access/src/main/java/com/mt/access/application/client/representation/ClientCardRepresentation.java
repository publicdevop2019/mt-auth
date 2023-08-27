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
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Data
@NoArgsConstructor
public class ClientCardRepresentation {

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

    public ClientCardRepresentation(Client client1) {
        id = client1.getClientId().getDomainId();
        name = client1.getName();
        grantTypeEnums = new HashSet<>();//avoid lazy load
        grantTypeEnums.addAll(client1.getGrantTypes());
        accessTokenValiditySeconds = client1.accessTokenValiditySeconds();
        description = client1.getDescription();
        if (client1.getRedirectDetail() != null) {
            registeredRedirectUri = client1.getRedirectDetail().getRedirectUrls().stream()
                .map(RedirectUrl::getValue).collect(Collectors.toSet());
        }
        if (client1.getTokenDetail() != null) {
            refreshTokenValiditySeconds = client1.getTokenDetail().getRefreshTokenValiditySeconds();
        }
        if (!ObjectUtils.isEmpty(client1.getResources())) {
            resourceIds = client1.getResources().stream().map(ClientId::getDomainId)
                .collect(Collectors.toSet());
        }
        resourceIndicator = client1.getAccessible();
        types = new HashSet<>();//avoid lazy load
        types.addAll(client1.getTypes());
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
