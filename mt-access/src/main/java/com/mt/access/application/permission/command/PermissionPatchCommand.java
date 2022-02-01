package com.mt.access.application.permission.command;

import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.role.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PermissionPatchCommand {
    private String name;

    public PermissionPatchCommand(Permission project) {
        this.name = project.getName();
    }
}
