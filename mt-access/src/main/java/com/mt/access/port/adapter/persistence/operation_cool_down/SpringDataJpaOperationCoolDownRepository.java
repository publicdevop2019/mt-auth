package com.mt.access.port.adapter.persistence.operation_cool_down;

import com.mt.access.domain.model.operation_cool_down.OperationCoolDown;
import com.mt.access.domain.model.operation_cool_down.OperationCoolDownRepository;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.common.domain.model.sql.DatabaseUtility;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * use pessimistic lock to prevent code from executing, due to third party api can not be un-do.
 */
public interface SpringDataJpaOperationCoolDownRepository
    extends JpaRepository<OperationCoolDown, Long>,
    OperationCoolDownRepository {

    @Query("SELECT p FROM #{#entityName} as p WHERE p.executor = ?1 AND p.operationType = ?2")
    Optional<OperationCoolDown> findByExecutorAndOperationType(String executor,
                                                               OperationType operationType);
    @Modifying
    @Query("UPDATE #{#entityName} as p SET p.lastOperateAt = ?1 WHERE p.executor = ?2 " +
        "AND p.operationType = ?3 AND p.lastOperateAt = ?4")
    Integer updateLastOptAt(Long now, String executor,
                            OperationType operationType, Long lastOptAt);

    default Optional<OperationCoolDown> query(String executor,
                                              OperationType operationType) {
        return findByExecutorAndOperationType(executor, operationType);
    }

    default void add(OperationCoolDown operationCoolDown) {
        save(operationCoolDown);
    }

    default void updateLastOperateAt(OperationCoolDown coolDown) {
        Integer integer = updateLastOptAt(Instant.now().toEpochMilli(), coolDown.getExecutor(),
            coolDown.getOperationType(),
            coolDown.getLastOperateAt());
        DatabaseUtility.checkUpdate(integer);
    }
}
