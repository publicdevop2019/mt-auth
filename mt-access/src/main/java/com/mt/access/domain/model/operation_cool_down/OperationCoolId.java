package com.mt.access.domain.model.operation_cool_down;

import java.io.Serializable;
import java.util.Objects;


public class OperationCoolId implements Serializable {
    private String executor;
    private OperationType operationType;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OperationCoolId that = (OperationCoolId) o;
        return Objects.equals(executor, that.executor) && operationType == that.operationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(executor, operationType);
    }
}
