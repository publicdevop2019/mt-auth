package com.mt.access.application.permission.representation;

import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionType;
import lombok.Data;

@Data
public class PermissionCardRepresentation {
    private String name;
    private String id;
    private String parentId;
    private PermissionType type;

    public PermissionCardRepresentation(Permission permission) {
        this.name = permission.getName();
        this.type = permission.getType();
        if (permission.getParentId() != null) {
            this.parentId = permission.getParentId().getDomainId();
        }
        this.id = permission.getPermissionId().getDomainId();
    }
}
