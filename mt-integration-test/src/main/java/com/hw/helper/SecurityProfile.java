package com.hw.helper;

import lombok.Data;

import java.util.Set;

@Data
public class SecurityProfile {
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
    private Integer version;
    private String description;
    private Set<String> clientRoles;
    private Set<String> userRoles;
    private Set<String> clientScopes;
    private boolean secured;
    private boolean userOnly;
    private boolean clientOnly;

}
