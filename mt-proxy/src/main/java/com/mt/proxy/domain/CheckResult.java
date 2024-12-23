package com.mt.proxy.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public enum CheckResult {
    PUBLIC_ENDPOINT(HttpStatus.OK),
    PERMISSION_ID_MATCH(HttpStatus.OK),
    MISSING_AUTH(HttpStatus.FORBIDDEN),
    INVALID_JWT(HttpStatus.UNAUTHORIZED),
    PARSE_ERROR(HttpStatus.FORBIDDEN),
    MISSING_RESOURCE_ID(HttpStatus.FORBIDDEN),
    MISSING_PERMISSION_ID(HttpStatus.FORBIDDEN),
    PERMISSION_ID_NOT_FOUND(HttpStatus.FORBIDDEN),
    NOT_FOUND_IN_RESOURCE(HttpStatus.FORBIDDEN);
    @Getter
    private final HttpStatus httpStatus;

    CheckResult(HttpStatus status) {
        this.httpStatus = status;
    }
}
