package com.mt.access.application.system_role.command;

import com.mt.access.domain.model.system_role.RoleType;
import lombok.Data;

@Data
public class CreateSystemRoleCommand {
    private RoleType type;
    private String name;
    private String description;
}
