package com.mt.access.application.role.command;

import java.util.Set;
import lombok.Data;

@Data
public class RoleUpdateCommand {
    private UpdateType type;
    private String name;
    private String projectId;
    private String parentId;
    private String description;
    private Set<String> commonPermissionIds;
    private Set<String> apiPermissionIds;
    private Set<String> externalPermissionIds;
}
