package com.mt.access.application.permission.command;

import lombok.Data;

import java.util.List;

@Data
public class PermissionCreateCommand {
    private String name;
    private String parentId;
    private String projectId;
    private List<String> linkedApiIds;
}
