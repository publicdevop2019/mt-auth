package com.mt.access.application.endpoint.command;

import java.io.Serializable;
import java.util.Set;
import lombok.Data;

@Data
public class EndpointUpdateCommand implements Serializable {
    private static final long serialVersionUID = 1;
    private String description;
    private String name;
    private Set<String> clientRoles;
    private boolean secured;
    private boolean isWebsocket;
    private boolean csrfEnabled;
    private String corsProfileId;
    private String resourceId;
    private String cacheProfileId;
    private String projectId;
    private int replenishRate;
    private int burstCapacity;
    private String path;

    private String method;
    private Integer version;
}
