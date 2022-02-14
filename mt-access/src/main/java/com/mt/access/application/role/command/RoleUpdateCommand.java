package com.mt.access.application.role.command;

import lombok.Data;

import java.util.Set;

@Data
public class RoleUpdateCommand {
    private String name;
    private String projectId;
    private String description;
    private Set<String> permissionIds;
}
