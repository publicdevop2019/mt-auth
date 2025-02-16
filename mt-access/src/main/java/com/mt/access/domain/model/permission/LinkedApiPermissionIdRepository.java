package com.mt.access.domain.model.permission;

import java.util.Map;
import java.util.Set;

public interface LinkedApiPermissionIdRepository {
    Set<PermissionId> query(Permission permission);

    void add(Permission permission, Set<PermissionId> permissionIds);

    void removeAll(Permission permission, Set<PermissionId> permissionIds);

    void addAll(Map<Permission, Set<PermissionId>> permissionLinkMap);

    void remove(PermissionId permissionId);
}
