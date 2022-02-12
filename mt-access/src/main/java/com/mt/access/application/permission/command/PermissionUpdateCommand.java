package com.mt.access.application.permission.command;

import lombok.Data;

@Data
public class PermissionUpdateCommand {
    private String name;
    private String parentId;
    private String linkedApiId;
}
