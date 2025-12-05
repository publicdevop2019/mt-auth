package com.mt.access.application.endpoint.command;

import lombok.Data;

@Data
public class RouterUpdateCommand {
    private String projectId;
    private String path;
    private String externalUrl;
    private String name;
    private String description;
}
