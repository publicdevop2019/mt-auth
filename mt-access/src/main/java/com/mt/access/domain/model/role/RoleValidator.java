package com.mt.access.domain.model.role;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class RoleValidator {
    private final ValidationNotificationHandler handler;
    private final Role role;

    public RoleValidator(ValidationNotificationHandler handler, Role role) {
        this.handler = handler;
        this.role = role;
    }

    public void validate() {
        checkName();
    }

    private void checkName() {
        if (!role.isSystemCreate()) {
            if (Role.reservedName.contains(role.getName())) {
                handler.handleError("reserved names not allowed");
            }
        }
    }
}
