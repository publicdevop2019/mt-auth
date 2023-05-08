package com.mt.access.domain.model.operation_cool_down;

import java.util.Optional;

public interface OperationCoolDownRepository {
    Optional<OperationCoolDown> by(String executor, OperationType operationType);

    void add(OperationCoolDown message);
}
