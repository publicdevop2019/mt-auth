package com.mt.access.application.client.command;

import com.mt.access.domain.model.client.GrantType;
import java.util.Set;
import lombok.Data;

@Data
public class ClientUpdateCommand {
    private String clientSecret;
    private String projectId;
    private Boolean hasSecret;
    private String description;
    private String name;
    private String path;
    private String externalUrl;
    private Set<GrantType> grantTypeEnums;
    private Integer accessTokenValiditySeconds;
    private Set<String> registeredRedirectUri;
    private Integer refreshTokenValiditySeconds;
    private Set<String> resourceIds;
    private Boolean resourceIndicator;
    private Boolean autoApprove;
    private Integer version;
}
