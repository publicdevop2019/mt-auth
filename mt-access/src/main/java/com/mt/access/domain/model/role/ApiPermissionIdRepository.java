package com.mt.access.domain.model.role;

import com.mt.access.domain.model.permission.PermissionId;
import java.util.Set;

public interface ApiPermissionIdRepository {
    Set<PermissionId> query(Role role);

    void add(Role role, Set<PermissionId> permissionIds);

    void removeAll(Role role, Set<PermissionId> permissionIds);

    void remove(Role role, Set<PermissionId> permissionIds);
    void remove(PermissionId permissionId);
}
