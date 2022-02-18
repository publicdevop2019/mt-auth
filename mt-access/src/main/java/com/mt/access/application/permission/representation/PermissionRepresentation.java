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
    private boolean systemCreate;
    private String linkedApiPermissionId;

    public PermissionRepresentation(Permission permission) {
        this.id = permission.getPermissionId().getDomainId();
        this.name = permission.getName();
        this.type = permission.getType();
        this.systemCreate = permission.isSystemCreate();
        if (permission.getParentId() != null) {
            this.parentId = permission.getParentId().getDomainId();
        }
        if (permission.getLinkedApiPermissionId() != null) {
            this.linkedApiPermissionId = permission.getLinkedApiPermissionId().getDomainId();
        }
    }
}
