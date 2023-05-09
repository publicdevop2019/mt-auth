package com.mt.access.domain.model.permission;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public interface PermissionRepository {
    void add(Permission role);

    SumPagedRep<Permission> query(PermissionQuery roleQuery);

    void remove(Permission e);

    void removeAll(Set<Permission> e);

    default Permission get(PermissionId id){
        Permission permission = query(id);
        Validator.notNull(permission);
        return permission;
    }
    default Permission get(ProjectId projectId, PermissionId id){
        PermissionQuery permissionQuery = new PermissionQuery(id, projectId);
        Permission permission = query(permissionQuery).findFirst().orElse(null);
        Validator.notNull(permission);
        return permission;
    }
    Permission query(PermissionId id);

    Set<EndpointId> allApiPermissionLinkedEpId();

    Set<PermissionId> allPermissionId();

    Set<PermissionId> getLinkedApiPermissionFor(Set<PermissionId> e);

    long countProjectCreateTotal(ProjectId projectId);
}
