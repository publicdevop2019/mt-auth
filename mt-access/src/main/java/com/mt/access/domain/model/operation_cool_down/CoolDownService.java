package com.mt.access.domain.model.operation_cool_down;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class CoolDownService {

    /**
     * check if operation has cool down or not, throw exception if not.
     *
     * @param executor      executor identifier
     * @param operationType operation type
     */
    public void hasCoolDown(String executor, OperationType operationType) {
        Validator.notNull(executor);
        Optional<OperationCoolDown> coolDownInfo =
            DomainRegistry.getOperationCoolDownRepository()
                .getCoolDownInfo(executor, operationType);
        if (coolDownInfo.isPresent()) {
            OperationCoolDown operationCoolDown = coolDownInfo.get();
            boolean cool = operationCoolDown.hasCoolDown();
            if (!cool) {
                throw new OperationNotCoolDownException();
            }
            log.info("operation has cool down");
            operationCoolDown.updateLastOperateAt();
        } else {
            log.info("new operation");
            OperationCoolDown coolDown = new OperationCoolDown(executor, operationType);
            DomainRegistry.getOperationCoolDownRepository().add(coolDown);
        }
    }
}