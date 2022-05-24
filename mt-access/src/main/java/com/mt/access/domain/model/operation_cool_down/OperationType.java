package com.mt.access.domain.model.operation_cool_down;

import com.mt.common.domain.model.sql.converter.EnumConverter;

/**
 * operation type enum.
 */
public enum OperationType {
    PWD_RESET,
    PENDING_USER_CODE;

    public static class DbConverter extends EnumConverter<OperationType> {
        public DbConverter() {
            super(OperationType.class);
        }
    }
}
