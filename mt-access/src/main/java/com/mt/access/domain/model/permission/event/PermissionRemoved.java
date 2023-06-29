package com.mt.access.domain.model.permission.event;

import com.mt.access.domain.model.permission.Permission;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class PermissionRemoved extends DomainEvent {

    public static final String PROJECT_PERMISSION_REMOVED = "project_permission_removed";
    public static final String name = "PROJECT_PERMISSION_REMOVED";
    {
        setTopic(PROJECT_PERMISSION_REMOVED);
        setName(name);
    }

    public PermissionRemoved(Permission permission) {
        super(permission.getPermissionId());
    }
}
