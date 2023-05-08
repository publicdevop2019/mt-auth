package com.mt.access.domain.model.role;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RoleValidator {
    private final ValidationNotificationHandler handler;
    private final Role role;

    public RoleValidator(ValidationNotificationHandler handler, Role role) {
        this.handler = handler;
        this.role = role;
    }

    public void validate() {
        checkName();
        checkPermission(role.getApiPermissionIds(), PermissionType.API);
        checkPermission(role.getCommonPermissionIds(), PermissionType.COMMON);
    }

    private void checkPermission(Set<PermissionId> permissionIds, PermissionType type) {
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    new PermissionQuery(permissionIds, type));
            if (log.isDebugEnabled() && allByQuery.size() != permissionIds.size()) {
                Set<PermissionId> collect =
                    allByQuery.stream().map(Permission::getPermissionId)
                        .collect(Collectors.toSet());
                Set<PermissionId> collect1 =
                    permissionIds.stream().filter(e -> !collect.contains(e))
                        .collect(Collectors.toSet());
                log.debug("unable find permission id(s) {}", collect1);
            }
            Validator
                .equalTo(allByQuery.size(), permissionIds.size(),
                    "unable to find all permissionIds");
        }
    }


    private void checkName() {
        if (!role.isSystemCreate()) {
            if (Role.reservedName.contains(role.getName())) {
                handler.handleError("reserved names not allowed");
            }
        }
    }
}
