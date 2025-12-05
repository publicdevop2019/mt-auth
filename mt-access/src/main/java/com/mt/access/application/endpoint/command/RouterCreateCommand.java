package com.mt.access.application.endpoint.command;

import lombok.Data;
import lombok.Getter;

@Data
public class RouterCreateCommand {
    private String projectId;
    private String path;
    private String externalUrl;
    private String name;
    private String description;
}
