package com.mt.access.domain.model.operation_cool_down;

/**
 * operation type enum, and it's cool down setting in milliseconds
 */
public enum OperationType {
    PWD_RESET(60 * 1000),
    VERIFICATION_CODE(60 * 1000);
    public final Integer coolDownMilli;


    OperationType(Integer coolDownMilli) {
        this.coolDownMilli = coolDownMilli;
    }
}
