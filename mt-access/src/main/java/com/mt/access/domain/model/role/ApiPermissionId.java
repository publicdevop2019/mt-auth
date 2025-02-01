package com.mt.access.domain.model.role;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.Utility;
import java.util.Set;

public class ApiPermissionId {
    public static void add(Role role, Set<PermissionId> apiPermissionIds,
                           Set<PermissionId> linkedApiPermission) {
        if (Checker.notNullOrEmpty(apiPermissionIds)) {
            Validator.noNullMember(apiPermissionIds);
            Validator.lessThanOrEqualTo(apiPermissionIds, 10);
            apiPermissionIds.addAll(linkedApiPermission);
            DomainRegistry.getApiPermissionIdRepository().add(role, apiPermissionIds);
        }
    }

    public static void update(Role role, Set<PermissionId> old, Set<PermissionId> next) {
        Utility.updateSet(old, next,
            (added) -> {
                Validator.noNullMember(added);
                Validator.lessThanOrEqualTo(next, 10);
                DomainRegistry.getApiPermissionIdRepository().add(role, added);
            },
            (removed) -> DomainRegistry.getApiPermissionIdRepository().remove(role, removed));
    }
}
