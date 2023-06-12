package com.mt.test_case.helper.pojo;

import lombok.Data;

@Data
public class Endpoint {
    private String resourceId;

    private String lookupPath;

    private String method;
    private String name;

    private String url;

    private String id;
    private String scheme;
    private String userInfo;
    private String host;
    private Integer port;
    private String path;
    private String query;
    private String fragment;
    private String cacheProfileId;
    private String corsProfileId;
    private Integer version;
    private String description;
    private Boolean websocket;
    private Boolean csrfEnabled;
    private Boolean secured;
    private Boolean external;
    private Boolean shared;
    private Integer replenishRate;

    private Integer burstCapacity;
}
