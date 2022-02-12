package com.mt.access.application.permission.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import lombok.Data;

@Data
public class PermissionRepresentation {
    private String name;
    private String id;
    private String parentId;
    private PermissionType type;
    private String linkedApiId;

    public PermissionRepresentation(Permission permission) {
        this.id = permission.getPermissionId().getDomainId();
        this.name = permission.getName();
        this.type = permission.getType();
        if (permission.getParentId() != null) {
            this.parentId = permission.getParentId().getDomainId();
        }
        if (permission.getLinkedApiPermissionId() != null) {
            this.linkedApiId = permission.getLinkedApiPermissionId().getDomainId();
        }
    }
}
