package com.mt.access.application.role.command;

import lombok.Data;

import java.util.Set;

@Data
public class RoleCreateCommand {
    private String name;
    private String parentId;
    private String projectId;
    private String description;
    private Set<String> permissionIds;
    private Set<String> externalPermissionIds;
}
