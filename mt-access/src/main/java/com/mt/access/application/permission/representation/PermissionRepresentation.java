package com.mt.access.application.permission.representation;

import com.mt.access.domain.model.permission.Permission;
import lombok.Data;

@Data
public class PermissionRepresentation {
    private String name;
    private String id;
    private String parentId;
    public PermissionRepresentation(Permission permission) {
        this.name= permission.getName();
        this.parentId= permission.getParentId().getDomainId();
        this.id= permission.getPermissionId().getDomainId();
    }
}
