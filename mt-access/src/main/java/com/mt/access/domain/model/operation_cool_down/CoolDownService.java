package com.mt.access.domain.model.operation_cool_down;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CoolDownService {

    /**
     * check if operation has cool down or not, throw exception if not.
     *
     * @param emailOrMobile      cool down key
     * @param operationType operation type
     */
    public void hasCoolDown(String emailOrMobile, OperationType operationType) {
        Validator.notNull(emailOrMobile);
        Optional<OperationCoolDown> coolDownInfo =
            DomainRegistry.getOperationCoolDownRepository()
                .query(emailOrMobile, operationType);
        if (coolDownInfo.isPresent()) {
            OperationCoolDown coolDown = coolDownInfo.get();
            boolean cool = coolDown.hasCoolDown();
            if (!cool) {
                throw new DefinedRuntimeException("operation not cool down", "1048",
                    HttpResponseCode.BAD_REQUEST);
            }
            log.info("operation has cool down");
            //it's ok if update success but following operation failed
            CommonDomainRegistry.getTransactionService().transactionalEvent((ignored) -> {
                DomainRegistry.getOperationCoolDownRepository().updateLastOperateAt(coolDown);
            });
        } else {
            log.info("new operation");
            CommonDomainRegistry.getTransactionService().transactionalEvent((ignored) -> {
                OperationCoolDown coolDown = new OperationCoolDown(emailOrMobile, operationType);
                DomainRegistry.getOperationCoolDownRepository().add(coolDown);
            });
        }
    }
}
