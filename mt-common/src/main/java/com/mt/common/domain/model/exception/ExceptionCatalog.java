package com.mt.common.domain.model.exception;

import lombok.Getter;

@Getter
public enum ExceptionCatalog {
    ILLEGAL_ARGUMENT("0001"),
    ILLEGAL_STATE("0002"),
    ACCESS_DENIED("0003"),
    OPERATION_ERROR("0004");

    private final String catalogCode;

    ExceptionCatalog(String i) {
        catalogCode = i;
    }
}
