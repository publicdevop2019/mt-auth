package com.mt.access.application.client.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import java.util.List;
import java.util.Optional;
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

    private int accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private int refreshTokenValiditySeconds;

    private Set<ResourceClientInfo> resources;

    private Set<String> resourceIds;

    private boolean resourceIndicator;

    private boolean autoApprove;

    private int version;

    public ClientCardRepresentation(Client client1) {
        id = client1.getClientId().getDomainId();
        name = client1.getName();
        grantTypeEnums = client1.getGrantTypes();
        accessTokenValiditySeconds = client1.accessTokenValiditySeconds();
        description = client1.getDescription();
        if (client1.getAuthorizationCodeGrant() != null) {
            registeredRedirectUri = client1.getAuthorizationCodeGrant().getRedirectUrls().stream()
                .map(RedirectUrl::getValue).collect(Collectors.toSet());
        }
        if (client1.getTokenDetail() != null) {
            refreshTokenValiditySeconds = client1.getTokenDetail().getRefreshTokenValiditySeconds();
        }
        if (!ObjectUtils.isEmpty(client1.getResources())) {
            resourceIds = client1.getResources().stream().map(ClientId::getDomainId)
                .collect(Collectors.toSet());
        }
        resourceIndicator = client1.isAccessible();
        types = client1.getTypes();
    }

    public static void updateDetails(List<ClientCardRepresentation> data) {
        Set<ClientId> collect = data.stream().filter(e -> e.getResourceIds() != null)
            .flatMap(e -> e.getResourceIds().stream()).map(ClientId::new)
            .collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            Set<Client> allByIds =
                ApplicationServiceRegistry.getClientApplicationService().findAllByIds(collect);
            data.forEach(e -> {
                if (e.getResourceIds() != null) {
                    e.resources = e.getResourceIds().stream().map(ee -> {
                        Optional<Client> first = allByIds.stream()
                            .filter(el -> el.getClientId().getDomainId().equals(ee)).findFirst();
                        return first.map(client -> new ResourceClientInfo(client.getName(), ee))
                            .orElseGet(() -> new ResourceClientInfo(ee, ee));
                    }).collect(Collectors.toSet());
                }
            });
        }
    }

    @Data
    private static class ResourceClientInfo {
        private String name;
        private String id;

        public ResourceClientInfo(String name, String id) {
            this.name = name;
            this.id = id;
        }
    }
}
