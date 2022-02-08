package com.mt.access.application.permission.representation;

import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionType;
import lombok.Data;

@Data
public class PermissionRepresentation {
    private String name;
    private String id;
    private String parentId;
    private PermissionType type;
    public PermissionRepresentation(Permission permission) {
        this.name= permission.getName();
        this.type = permission.getType();
        this.parentId= permission.getParentId().getDomainId();
        this.id= permission.getPermissionId().getDomainId();
    }
}
