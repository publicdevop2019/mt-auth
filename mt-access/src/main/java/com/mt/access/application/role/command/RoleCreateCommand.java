package com.mt.access.application.role.command;

import java.util.Set;
import lombok.Data;

@Data
public class RoleCreateCommand {
    private String name;
    private String parentId;
    private String projectId;
    private String description;
    private Set<String> permissionIds;
    private Set<String> externalPermissionIds;
}
