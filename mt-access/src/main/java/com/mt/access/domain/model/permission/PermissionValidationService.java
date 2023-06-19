package com.mt.access.domain.model.permission;

import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PermissionValidationService {
    public void validate(Permission permission, PermissionCreateCommand command,
                         ValidationNotificationHandler handler) {
        validateLinkedPermission(permission, command, handler);
        validateParentId(permission);
    }

    private void validateParentId(Permission permission) {
        if (Checker.notNull(permission.getParentId())) {
            PermissionQuery permissionQuery =
                PermissionQuery.tenantQuery(permission.getProjectId(), permission.getParentId());
            Permission permission1 =
                DomainRegistry.getPermissionRepository().query(permissionQuery).findFirst().orElse(
                    null);
            Validator.notNull(permission1);
        }
    }

    private void validateLinkedPermission(Permission permission, PermissionCreateCommand command,
                                          ValidationNotificationHandler handler) {
        List<String> linkedApiIds = command.getLinkedApiIds();
        if (Checker.notNull(linkedApiIds) && Checker.notEmpty(linkedApiIds)) {
            if (Checker.isNull(permission.getLinkedApiPermissionIds()) ||
                Checker.sizeNotEquals(permission.getLinkedApiPermissionIds(), linkedApiIds)) {
                handler.handleError("not all request linked api added");
            }
        }
    }
}
