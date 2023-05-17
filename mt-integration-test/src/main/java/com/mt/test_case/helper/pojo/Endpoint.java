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
    private boolean websocket;
    private String fragment;
    private String cacheProfileId;
    private String corsProfileId;
    private Integer version;
    private String description;
    private boolean secured;
    private boolean external;
    private boolean shared;
    private int replenishRate = 10;

    private int burstCapacity = 10;
}
