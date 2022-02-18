package com.mt.access.domain.model.permission;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class PermissionValidator {
    private final ValidationNotificationHandler handler;
    private final Permission permission;

    public PermissionValidator(ValidationNotificationHandler handler, Permission permission) {
        this.handler = handler;
        this.permission = permission;
    }

    public void validate() {
        checkName();
    }

    private void checkName() {
        if (!permission.isSystemCreate()) {
            if (Permission.reservedName.contains(permission.getName())) {
                handler.handleError("certain names are not allowed for non system create role");
            }
        }

    }
}
