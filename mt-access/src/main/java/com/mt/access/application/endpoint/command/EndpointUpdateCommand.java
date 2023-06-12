package com.mt.access.application.endpoint.command;

import lombok.Data;

@Data
public class EndpointUpdateCommand {
    private String name;
    private String description;
    private Boolean websocket;
    private Boolean csrfEnabled;
    private String corsProfileId;
    private String cacheProfileId;
    private String projectId;
    private Integer replenishRate;
    private Integer burstCapacity;
    private String path;
    private String method;
    private Integer version;
}
