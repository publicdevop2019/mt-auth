package com.mt.access.domain.model.operation_cool_down;

import com.mt.access.domain.DomainRegistry;
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
     * @param domainId      domain id
     * @param operationType operation type
     */
    public void hasCoolDown(String domainId, OperationType operationType) {
        Validator.notNull(domainId);
        Optional<OperationCoolDown> coolDownInfo =
            DomainRegistry.getOperationCoolDownRepository()
                .query(domainId, operationType);
        if (coolDownInfo.isPresent()) {
            OperationCoolDown coolDown = coolDownInfo.get();
            boolean cool = coolDown.hasCoolDown();
            if (!cool) {
                throw new DefinedRuntimeException("operation not cool down", "1048",
                    HttpResponseCode.BAD_REQUEST);
            }
            log.info("operation has cool down");
            coolDown.updateLastOperateAt();
        } else {
            log.info("new operation");
            OperationCoolDown coolDown = new OperationCoolDown(domainId, operationType);
            DomainRegistry.getOperationCoolDownRepository().add(coolDown);
        }
    }
}
