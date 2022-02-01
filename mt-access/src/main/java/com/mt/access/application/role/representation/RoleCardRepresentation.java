package com.mt.access.application.role.representation;

import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.role.Role;
import lombok.Data;

@Data
public class RoleCardRepresentation {
    private final String name;
    private final String description;
    private final String id;

    public RoleCardRepresentation(Role role) {
        this.name = role.getName();
        this.description = role.getDescription();
        this.id = role.getRoleId().getDomainId();
    }
}
