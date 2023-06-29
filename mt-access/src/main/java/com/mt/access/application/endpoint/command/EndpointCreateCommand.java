package com.mt.access.application.endpoint.command;

import lombok.Data;

@Data
public class EndpointCreateCommand {
    private String name;
    private String description;
    private String projectId;
    private Boolean secured;
    private Boolean websocket;
    private Boolean csrfEnabled;
    private Boolean shared;
    private String corsProfileId;
    private String cacheProfileId;
    private String resourceId;
    private String path;
    private Boolean external;
    private Integer replenishRate;
    private Integer burstCapacity;
    private String method;
}
