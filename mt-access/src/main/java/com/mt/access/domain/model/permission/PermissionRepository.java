package com.mt.access.domain.model.permission;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface PermissionRepository {
    void add(Permission role);

    SumPagedRep<Permission> getByQuery(PermissionQuery roleQuery);

    void remove(Permission e);

    Optional<Permission> getById(PermissionId id);
}
