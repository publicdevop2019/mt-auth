package com.mt.access.domain.model.role;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.Utility;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class CommonPermissionId {
    public static Set<PermissionId> add(Role role, Set<PermissionId> commonPermissionIds) {
        Set<PermissionId> linkedApiPermission = Collections.emptySet();
        if (Checker.notNullOrEmpty(commonPermissionIds)) {
            Set<Permission> permissions = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(commonPermissionIds));
            //add linked api permission
            linkedApiPermission =
                permissions.stream().flatMap(
                    e -> DomainRegistry.getLinkedApiPermissionIdRepository().query(e)
                        .stream()).collect(Collectors.toSet());

            boolean b =
                permissions.stream().map(Permission::getTenantId)
                    .collect(Collectors.toSet())
                    .size() > 1;
            if (b) {
                throw new DefinedRuntimeException(
                    "permissions added to role must belong to same tenant project", "1053",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (Checker.notNullOrEmpty(commonPermissionIds)) {
                Validator.noNullMember(commonPermissionIds);
                Validator.lessThanOrEqualTo(commonPermissionIds, 10);
            }
            DomainRegistry.getCommonPermissionIdRepository().add(role, commonPermissionIds);
        }
        return linkedApiPermission;
    }

    public static void update(Role role, Set<PermissionId> old, Set<PermissionId> next) {
        Utility.updateSet(old, next,
            (added) -> {
                Validator.noNullMember(added);
                Validator.lessThanOrEqualTo(next, 10);
                DomainRegistry.getCommonPermissionIdRepository().add(role, added);
            },
            (removed) -> DomainRegistry.getCommonPermissionIdRepository()
                .remove(role, removed));
    }
}
