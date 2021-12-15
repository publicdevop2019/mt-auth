package com.mt.access.application.system_role.command;

import com.mt.access.domain.model.system_role.SystemRole;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PatchSystemRoleCommand {
    private String name;
    private String description;

    public PatchSystemRoleCommand(SystemRole original) {
        this.name=original.getName();
        this.description=original.getDescription();
    }
}
