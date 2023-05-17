package com.mt.test_case.helper.pojo;

import lombok.Data;

import java.util.Set;

@Data
public class Client {

    private String id;
    private String clientSecret;
    private String description;
    private String name;
    private String path;
    private String externalUrl;
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
