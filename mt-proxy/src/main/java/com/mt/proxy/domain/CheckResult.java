package com.mt.proxy.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum CheckResult {
    MISSING_AUTH(HttpStatus.FORBIDDEN),
    INVALID_JWT(HttpStatus.FORBIDDEN),
    PARSE_ERROR(HttpStatus.FORBIDDEN),
    EMPTY_CACHE(HttpStatus.FORBIDDEN),
    UNREGISTERED_PUBLIC_OR_NO_AUTH(HttpStatus.FORBIDDEN),
    MISSING_RESOURCE_ID(HttpStatus.FORBIDDEN),
    UNREGISTERED(HttpStatus.FORBIDDEN),
    MISSING_PERMISSION_ID(HttpStatus.FORBIDDEN),
    PERMISSION_ID_NOT_FOUND(HttpStatus.FORBIDDEN),
    PUBLIC_ENDPOINT(HttpStatus.OK),
    PERMISSION_ID_MATCH(HttpStatus.OK),
    NOT_FOUND_OR_DUPLICATE(HttpStatus.FORBIDDEN);
    @Getter
    private final HttpStatus httpStatus;

    CheckResult(HttpStatus status) {
        this.httpStatus = status;
    }
}
