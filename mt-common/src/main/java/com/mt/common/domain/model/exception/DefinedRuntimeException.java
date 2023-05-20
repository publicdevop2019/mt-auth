package com.mt.common.domain.model.exception;

import lombok.Getter;

@Getter
public class DefinedRuntimeException extends RuntimeException {
    private final String errorCode;
    private final HttpResponseCode responseType;

    public DefinedRuntimeException(String message, String errorCode, HttpResponseCode responseCode) {
        super(message);
        this.errorCode = errorCode;
        this.responseType = responseCode;
    }

    public DefinedRuntimeException(String message, String errorCode, HttpResponseCode responseCode, Throwable ex) {
        super(message, ex);
        this.errorCode = errorCode;
        this.responseType = responseCode;
    }
}
