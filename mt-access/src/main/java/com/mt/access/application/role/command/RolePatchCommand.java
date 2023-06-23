package com.mt.access.application.role.command;

import com.mt.access.domain.model.role.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RolePatchCommand {
    private String name;
    private String description;

    public RolePatchCommand(Role role) {
        this.name = role.getName();
        this.description = role.getDescription();
    }
}
