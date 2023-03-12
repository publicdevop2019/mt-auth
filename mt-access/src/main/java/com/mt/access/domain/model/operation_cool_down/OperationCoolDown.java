package com.mt.access.domain.model.operation_cool_down;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import com.mt.common.domain.model.audit.NextAuditable;
import java.time.Instant;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"executor",
    "opt_type"}), name = "opt_cool_down")
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
public class OperationCoolDown extends NextAuditable {
    /**
     * person who execute this operation, can be email or user id.
     */
    @Column(name = "executor")
    private String executor;

    @Column(name = "opt_type")
    @Convert(converter = OperationType.DbConverter.class)
    private OperationType operationType;

    @Column(name = "last_opt_at")
    private Date lastOperateAt;

    /**
     * constructor.
     *
     * @param executor      identifier for who execute this operation
     * @param operationType operation type enum
     */
    public OperationCoolDown(String executor, OperationType operationType) {
        super();
        setId(CommonDomainRegistry.getUniqueIdGeneratorService().id());
        setExecutor(executor);
        setOperationType(operationType);
        setLastOperateAt(Date.from(Instant.now()));
    }

    /**
     * operation has 1 minute cool down.
     *
     * @return boolean if cool down or not
     */
    public boolean hasCoolDown() {
        return System.currentTimeMillis() > lastOperateAt.getTime() + 60 * 1000;
    }

    public void updateLastOperateAt() {
        lastOperateAt = new Date(System.currentTimeMillis());
    }
}
