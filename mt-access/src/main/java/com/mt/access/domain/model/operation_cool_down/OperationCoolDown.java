package com.mt.access.domain.model.operation_cool_down;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.audit.Auditable;
import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;


@Entity
@Data
@Table(name = "opt_cool_down")
@NoArgsConstructor
@Setter(AccessLevel.PRIVATE)
@IdClass(OperationCoolId.class)
public class OperationCoolDown{
    @Version
    @Setter(AccessLevel.PRIVATE)
    @Getter
    private Integer version;

    /**
     * person who execute this operation, can be email or user id.
     */
    @Id
    @Column(name = "executor")
    private String executor;

    @Id
    @Column(name = "opt_type")
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    @Column(name = "last_opt_at")
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

    /**
     * operation has 1 minute cool down.
     *
     * @return boolean if cool down or not
     */
    public boolean hasCoolDown() {
        return Instant.now().toEpochMilli() > lastOperateAt + 60 * 1000;
    }

    public void updateLastOperateAt() {
        lastOperateAt = Instant.now().toEpochMilli();
    }
}
