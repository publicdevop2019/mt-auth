package com.mt.access.domain.model.operation_cool_down;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class OperationCoolDown {
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Integer version;

    /**
     * person who execute this operation, can be email or user id.
     */
    private String executor;

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

    public static OperationCoolDown fromDatabaseRow(Integer version, OperationType optType,
                                                    String executor, Long lastOptAt) {
        OperationCoolDown coolDown = new OperationCoolDown();
        coolDown.setVersion(version);
        coolDown.setOperationType(optType);
        coolDown.setExecutor(executor);
        coolDown.setLastOperateAt(lastOptAt);
        return coolDown;
    }

    /**
     * operation has 1 minute cool down.
     *
     * @return boolean if cool down or not
     */
    public boolean hasCoolDown() {
        return Instant.now().toEpochMilli() > lastOperateAt + 60 * 1000;
    }
}
