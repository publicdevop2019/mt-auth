package com.mt.common.domain.model.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum HttpResponseCode {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    FORBIDDEN(HttpStatus.FORBIDDEN),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    NOT_HTTP(HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus httpCode;

    HttpResponseCode(HttpStatus status) {
        httpCode = status;
    }
}
