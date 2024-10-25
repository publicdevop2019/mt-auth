package com.mt.access.domain.model.operation_cool_down;

import com.mt.common.domain.model.exception.DefinedRuntimeException;

public interface CoolDownService {

    /**
     * check if operation has cool down or not, throw exception if not.
     *
     * @param coolDownKey   cool down key
     * @param operationType operation type
     * @throws DefinedRuntimeException cool down check failed
     */
    void hasCoolDown(String coolDownKey, OperationType operationType);
}
