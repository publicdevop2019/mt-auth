package com.mt.access.domain.model.permission;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;

public interface PermissionRepository {
    void add(Permission role);

    SumPagedRep<Permission> getByQuery(PermissionQuery roleQuery);

    void remove(Permission e);

    void removeAll(Set<Permission> e);

    Optional<Permission> getById(PermissionId id);

    Set<EndpointId> allApiPermissionLinkedEpId();

    Set<PermissionId> allPermissionId();

    Set<PermissionId> getLinkedApiPermissionFor(Set<PermissionId> e);
}
