package com.mt.access.domain.model.permission;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PermissionValidator {
    private final ValidationNotificationHandler handler;
    private final Permission permission;

    public PermissionValidator(ValidationNotificationHandler handler, Permission permission) {
        this.handler = handler;
        this.permission = permission;
    }

    public void validate() {
        checkName();
        checkNotNullValue();
        validateParentId();
    }

    private void validateParentId() {
        if (Checker.notNull(permission.getParentId()) &&
            Checker.isFalse(permission.getSystemCreate())) {
            PermissionQuery permissionQuery =
                PermissionQuery.tenantQuery(permission.getProjectId(), permission.getParentId());
            log.debug("validating permission parent id {} with project id {}",
                permission.getParentId(), permission.getProjectId());
            Permission permission1 =
                DomainRegistry.getPermissionRepository().query(permissionQuery).findFirst().orElse(
                    null);
            Validator.notNull(permission1);
        }
    }

    private void checkNotNullValue() {
        Validator.notNull(permission.getSystemCreate());
        Validator.notNull(permission.getShared());
        Validator.notNull(permission.getType());
        Validator.notNull(permission.getPermissionId());
        Validator.notNull(permission.getProjectId());
        Validator.notNull(permission.getName());
    }

    private void checkName() {
        if (Checker.isFalse(permission.getSystemCreate())) {
            if (permission.getName() == null ||
                Permission.reservedName.contains(permission.getName()) ||
                permission.getName().startsWith(
                    ProjectId.getIdPrefix())) {
                handler.handleError(
                    "value is not allowed for non system create role");
            }
        }

    }
}
