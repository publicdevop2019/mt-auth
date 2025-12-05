package com.mt.access.application.client.representation;

import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.common.infrastructure.Utility;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ClientMgmtCardRepresentation {

    private String id;

    private String name;

    private String description;

    private Set<GrantType> grantTypeEnums;

    private ClientType type;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Boolean autoApprove;

    private Integer version;

    public ClientMgmtCardRepresentation(Client client, Set<RedirectUrl> urls,
                                        Set<GrantType> grantTypes) {
        id = client.getClientId().getDomainId();
        name = client.getName();
        grantTypeEnums = grantTypes;
        accessTokenValiditySeconds = client.accessTokenValiditySeconds();
        description = client.getDescription();
        registeredRedirectUri = Utility.mapToSet(urls, RedirectUrl::getValue);
        if (client.getTokenDetail() != null) {
            refreshTokenValiditySeconds = client.getTokenDetail().getRefreshTokenValiditySeconds();
        }
        type = client.getType();
    }
}
