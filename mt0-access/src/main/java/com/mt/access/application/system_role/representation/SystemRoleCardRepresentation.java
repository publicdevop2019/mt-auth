package com.mt.access.application.system_role.representation;

import com.mt.access.domain.model.system_role.RoleType;
import com.mt.access.domain.model.system_role.SystemRole;
import lombok.Data;

@Data
public class SystemRoleCardRepresentation {
    private String id;
    private RoleType type;
    private String name;
    private String description;
    public SystemRoleCardRepresentation(SystemRole systemRole) {
        this.id=systemRole.getRoleId().getDomainId();
        this.type =systemRole.getRoleType();
        this.name=systemRole.getName();
        this.description=systemRole.getDescription();

    }
}
