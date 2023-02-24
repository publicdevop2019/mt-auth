package com.mt.common.domain.model.exception;

import lombok.Getter;

@Getter
public class DefinedRuntimeException extends RuntimeException {
    private final String errorCode;
    private final HttpResponseCode responseType;
    private final ExceptionCatalog catalog;

    public DefinedRuntimeException(String message, String errorCode, HttpResponseCode responseCode,
                                   ExceptionCatalog catalog) {
        super(message);
        this.errorCode = errorCode;
        this.responseType = responseCode;
        this.catalog = catalog;
    }

    public DefinedRuntimeException(String message, String errorCode, HttpResponseCode responseCode,
                                   ExceptionCatalog catalog, Throwable ex) {
        super(message, ex);
        this.errorCode = errorCode;
        this.responseType = responseCode;
        this.catalog = catalog;
    }

    public String getCombinedErrorCode() {
        return this.catalog.getCatalogCode() + "_" + this.errorCode;
    }
}
