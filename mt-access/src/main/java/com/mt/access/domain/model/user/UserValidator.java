package com.mt.access.domain.model.user;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

import javax.validation.constraints.NotNull;

public class UserValidator {
    private final User user;
    private final ValidationNotificationHandler handler;

    public UserValidator(User user, @NotNull ValidationNotificationHandler handler) {
        this.user = user;
        this.handler = handler;
    }

    public void validate() {

    }

}
