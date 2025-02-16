package com.mt.access.application.client.command;

import com.mt.access.domain.model.client.ClientType;
import com.mt.access.domain.model.client.GrantType;
import java.util.Set;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ClientCreateCommand {
    private String clientSecret;
    private String projectId;
    private String description;
    private String name;
    private String path;
    private String externalUrl;
    private Set<GrantType> grantTypeEnums;
    private ClientType type;
    private Integer accessTokenValiditySeconds;
    private Set<String> registeredRedirectUri;
    private Integer refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private Boolean resourceIndicator;
}
