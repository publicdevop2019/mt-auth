package com.mt.access.application.role.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleType;
import com.mt.common.domain.model.domainId.DomainId;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RoleRepresentation {
    private final RoleType roleType;
    private String id;
    private String name;
    private String originalName;
    private String parentId;
    private Set<String> permissionIds;
    private Set<String> externalPermissionIds;
    private boolean systemCreate;

    public RoleRepresentation(Role role) {
        this.id = role.getRoleId().getDomainId();
        this.name = role.getName();
        if (role.getParentId() != null)
            this.parentId = role.getParentId().getDomainId();
        this.originalName = role.getName();
        this.systemCreate = role.isSystemCreate();
        this.roleType = role.getType();
        if (role.getPermissionIds() != null) {
            this.permissionIds = role.getPermissionIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        }
        if (role.getExternalPermissionIds() != null) {
            this.externalPermissionIds = role.getExternalPermissionIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        }
        if (this.roleType.equals(RoleType.CLIENT)) {
            DomainRegistry.getClientRepository().clientOfId(new ClientId(role.getName())).ifPresent(e -> this.name = e.getName());
        }
        if (this.roleType.equals(RoleType.PROJECT)) {
            DomainRegistry.getProjectRepository().getById(new ProjectId(role.getName())).ifPresent(e -> this.name = e.getName());
        }
    }
}
