package com.mt.access.application.endpoint.command;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class EndpointUpdateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String description;
    private String name;
    private Set<String> clientRoles;
    private boolean secured;
    private String roleId;
    private boolean isWebsocket;
    private boolean csrfEnabled;
    private String corsProfileId;
    private String resourceId;
    private String cacheProfileId;

    private String path;

    private String method;
    private Integer version;
}
