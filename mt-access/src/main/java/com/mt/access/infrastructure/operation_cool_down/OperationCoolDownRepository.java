package com.mt.access.infrastructure.operation_cool_down;

import com.mt.access.domain.model.operation_cool_down.OperationType;
import java.util.Optional;

public interface OperationCoolDownRepository {
    Optional<OperationCoolDown> query(String executor, OperationType operationType);

    void add(OperationCoolDown coolDown);

    void updateLastOperateAt(OperationCoolDown coolDown);
}
