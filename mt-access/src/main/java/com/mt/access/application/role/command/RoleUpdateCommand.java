package com.mt.access.application.role.command;

import lombok.Data;

import java.util.Set;

@Data
public class RoleUpdateCommand {
    private String name;
    private Set<String> permissionIds;
}