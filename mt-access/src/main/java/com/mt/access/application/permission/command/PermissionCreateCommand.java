package com.mt.access.application.permission.command;

import lombok.Data;

@Data
public class PermissionCreateCommand {
    private String name;
    private String parentId;
    private String projectId;
}
