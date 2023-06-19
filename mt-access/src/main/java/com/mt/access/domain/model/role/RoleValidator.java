package com.mt.access.domain.model.role;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;
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
        reservedNameCannotBeUsed();
    }

    private void reservedNameCannotBeUsed() {
        if (!role.getSystemCreate()) {
            if (Role.reservedName.contains(role.getName())) {
                handler.handleError("reserved names not allowed");
            }
        }
    }
}
