package com.mt.access.infrastructure.operation_cool_down;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.operation_cool_down.CoolDownService;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JdbcCoolDownService implements CoolDownService {

    private static final String ERROR_MSG = "operation not cool down";
    private static final String ERROR_CODE = "1048";
    private static final HttpResponseCode ERROR_RESPONSE_CODE = HttpResponseCode.BAD_REQUEST;

    public void hasCoolDown(String coolDownKey, OperationType operationType) {
        Validator.notNull(coolDownKey);
        Optional<OperationCoolDown> coolDownInfo =
            DomainRegistry.getOperationCoolDownRepository().query(coolDownKey, operationType);
        if (coolDownInfo.isPresent()) {
            OperationCoolDown coolDown = coolDownInfo.get();
            boolean cool = coolDown.hasCoolDown();
            if (!cool) {
                throw new DefinedRuntimeException(ERROR_MSG, ERROR_CODE,
                    ERROR_RESPONSE_CODE);
            }
            log.info("operation has cool down");
            //it's ok if update success but following operation failed
            try {
                CommonDomainRegistry.getTransactionService().transactionalEvent((ignored) -> {
                    DomainRegistry.getOperationCoolDownRepository().updateLastOperateAt(coolDown);
                });
            } catch (Exception ex) {
                log.warn("operation concurrent access error", ex);
                throw new DefinedRuntimeException(ERROR_MSG, ERROR_CODE,
                    HttpResponseCode.BAD_REQUEST);
            }
        } else {
            log.info("new operation");
            try {
                CommonDomainRegistry.getTransactionService().transactionalEvent((ignored) -> {
                    OperationCoolDown coolDown = new OperationCoolDown(coolDownKey, operationType);
                    DomainRegistry.getOperationCoolDownRepository().add(coolDown);
                });
            } catch (Exception ex) {
                log.warn("operation concurrent access error", ex);
                throw new DefinedRuntimeException(ERROR_MSG, ERROR_CODE,
                    HttpResponseCode.BAD_REQUEST);
            }
        }
    }
}
