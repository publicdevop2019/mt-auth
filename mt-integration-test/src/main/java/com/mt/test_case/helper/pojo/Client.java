package com.mt.test_case.helper.pojo;

import java.util.Set;
import lombok.Data;

@Data
public class Client {

    private String id;
    private String clientSecret;
    private String description;
    private String name;
    private String path;
    private String externalUrl;
    private Set<String> types;

    private Set<String> grantTypeEnums;

    private Integer accessTokenValiditySeconds;

    private Set<String> registeredRedirectUri;

    private Integer refreshTokenValiditySeconds;

    private Set<String> resourceIds;

    private Boolean resourceIndicator;

    private Boolean autoApprove;

    private Boolean hasSecret;
    private Integer version;

}
