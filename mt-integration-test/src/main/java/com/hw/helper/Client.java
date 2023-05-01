package com.hw.helper;

import lombok.Data;

import java.util.Set;

@Data
public class Client {

    private String id;
    private String clientSecret;
    private String description;
    private String name;
    private Set<ClientType> types;

    private Set<GrantType> grantTypeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;

    private Boolean hasSecret;
    private Integer version;

}
