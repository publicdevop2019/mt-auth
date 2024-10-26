package com.mt.access.infrastructure.operation_cool_down;

import com.mt.access.domain.model.operation_cool_down.OperationType;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class OperationCoolDown {

    /**
     * person who execute this operation, can be email or user id.
     * primary key
     */
    private String executor;
    /**
     * primary key
     */
    private OperationType operationType;

    private Long lastOperateAt;

    /**
     * constructor.
     *
     * @param executor      identifier for who execute this operation
     * @param operationType operation type enum
     */
    public OperationCoolDown(String executor, OperationType operationType) {
        super();
        setExecutor(executor);
        setOperationType(operationType);
        setLastOperateAt(Instant.now().toEpochMilli());
    }

    public static OperationCoolDown fromDatabaseRow(OperationType optType,
                                                    String executor, Long lastOptAt) {
        OperationCoolDown coolDown = new OperationCoolDown();
        coolDown.setOperationType(optType);
        coolDown.setExecutor(executor);
        coolDown.setLastOperateAt(lastOptAt);
        return coolDown;
    }

    /**
     * check if operation has cool down or not.
     */
    public boolean hasCoolDown() {
        return Instant.now().toEpochMilli() > lastOperateAt + operationType.coolDownMilli;
    }
}
