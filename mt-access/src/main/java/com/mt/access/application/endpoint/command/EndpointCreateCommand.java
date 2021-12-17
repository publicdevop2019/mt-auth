package com.mt.access.application.endpoint.command;

import lombok.Data;

import java.io.Serializable;
import java.util.Set;

@Data
public class EndpointCreateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String description;
    private Set<String> clientRoles;
    private boolean secured;
    private boolean isWebsocket;
    private boolean csrfEnabled;
    private String corsProfileId;
    private String cacheProfileId;
    private String resourceId;
    private String roleId;

    private String path;

    private String method;
}
