package com.mt.common.infrastructure;

import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;

public class HttpValidationNotificationHandler implements ValidationNotificationHandler {
    public void handleError(String error) {
        throw new DefinedRuntimeException(error, "0004",
            HttpResponseCode.BAD_REQUEST,
            ExceptionCatalog.ILLEGAL_ARGUMENT);
    }
}
