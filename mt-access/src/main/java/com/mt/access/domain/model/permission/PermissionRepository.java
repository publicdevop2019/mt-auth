package com.mt.access.domain.model.permission;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public interface PermissionRepository {
    void add(Permission permission);

    void addAll(Set<Permission> permissions);

    SumPagedRep<Permission> query(PermissionQuery permissionQuery);

    SumPagedRep<PermissionId> queryPermissionId(PermissionQuery query);

    void remove(Permission e);

    default Permission get(PermissionId id) {
        Permission permission = query(id);
        Validator.notNull(permission);
        return permission;
    }

    Permission query(PermissionId id);

    Set<EndpointId> allApiPermissionLinkedEpId();

    Set<PermissionId> allPermissionId();

    Set<PermissionId> getLinkedApiPermissionFor(Set<PermissionId> e);

    long countProjectCreateTotal(ProjectId projectId);

    void removeLinkedApiPermission(PermissionId permissionId);
}
