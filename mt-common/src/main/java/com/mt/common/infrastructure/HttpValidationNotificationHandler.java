package com.mt.common.infrastructure;

import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class HttpValidationNotificationHandler implements ValidationNotificationHandler {
    public void handleError(String error) {
        throw new IllegalArgumentException(error);
    }
}
