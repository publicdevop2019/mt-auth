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

    default Permission byId(PermissionId id){
        Permission byIdNullable = byNullable(id);
        Validator.notNull(byIdNullable);
        return byIdNullable;
    }
    Permission byNullable(PermissionId id);

    Set<EndpointId> allApiPermissionLinkedEpId();

    Set<PermissionId> allPermissionId();

    Set<PermissionId> getLinkedApiPermissionFor(Set<PermissionId> e);

    long countProjectCreateTotal(ProjectId projectId);
}
