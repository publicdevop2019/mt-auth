package com.mt.access.application.role.representation;

import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleType;
import lombok.Data;

@Data
public class RoleCardRepresentation {
    private final String description;
    private final RoleType roleType;
    private final String id;
    private String name;
    private String tenantId;
    private Boolean systemCreate;

    public RoleCardRepresentation(Role role) {
        this.id = role.getRoleId().getDomainId();
        this.name = role.getName();
        this.systemCreate = role.getSystemCreate();
        this.description = role.getDescription();
        this.roleType = role.getType();
        if (role.getTenantId() != null) {
            this.tenantId = role.getTenantId().getDomainId();
        }
    }
}
