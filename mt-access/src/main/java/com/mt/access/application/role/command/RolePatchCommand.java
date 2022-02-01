package com.mt.access.application.role.command;

import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.role.Role;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RolePatchCommand {
    private String name;

    public RolePatchCommand(Role project) {
        this.name = project.getName();
    }
}
