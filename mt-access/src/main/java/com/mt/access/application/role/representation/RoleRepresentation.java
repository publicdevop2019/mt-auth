package com.mt.access.application.role.representation;

import com.mt.access.domain.model.role.Role;
import lombok.Data;

@Data
public class RoleRepresentation {
    private String name;
    public RoleRepresentation(Role role) {
        this.name= role.getName();
    }
}
