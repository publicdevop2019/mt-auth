package com.mt.access.port.adapter.persistence.operation_cool_down;

import com.mt.access.domain.model.operation_cool_down.OperationCoolDown;
import com.mt.access.domain.model.operation_cool_down.OperationCoolDownRepository;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

/**
 * use pessimistic lock to prevent code from executing, due to third party api can not be un-do.
 */
public interface SpringDataJpaOperationCoolDownRepository
    extends JpaRepository<OperationCoolDown, Long>,
    OperationCoolDownRepository {
    @Lock(LockModeType.PESSIMISTIC_FORCE_INCREMENT)
    @Query("SELECT p FROM #{#entityName} as p WHERE p.executor = ?1 AND p.operationType = ?2")
    Optional<OperationCoolDown> findByExecutorAndOperationType(String executor,
                                                               OperationType operationType);

    default Optional<OperationCoolDown> query(String executor,
                                              OperationType operationType) {
        return findByExecutorAndOperationType(executor, operationType);
    }

    default void add(OperationCoolDown operationCoolDown) {
        save(operationCoolDown);
    }
}
