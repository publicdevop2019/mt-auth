package com.mt.access.application.role.representation;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.Role;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RoleRepresentation {
    private String id;
    private String name;
    private Set<String> permissionIds;
    public RoleRepresentation(Role role) {
        this.id= role.getRoleId().getDomainId();
        this.name= role.getName();
        this.permissionIds=role.getPermissionIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
    }
}
