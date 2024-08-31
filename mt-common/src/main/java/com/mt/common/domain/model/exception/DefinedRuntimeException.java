package com.mt.common.domain.model.exception;

import lombok.Getter;

/**
 * known exceptions, should be documented and handled or unhandled on purpose
 */
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
