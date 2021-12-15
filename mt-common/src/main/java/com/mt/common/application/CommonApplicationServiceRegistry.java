package com.mt.common.application;

import com.mt.common.application.domain_event.StoredEventApplicationService;
import com.mt.common.application.idempotent.ChangeRecordApplicationService;
import com.mt.common.domain.model.idempotent.IdempotentService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonApplicationServiceRegistry {
    @Getter
    private static ChangeRecordApplicationService changeRecordApplicationService;
    @Getter
    private static StoredEventApplicationService storedEventApplicationService;
    @Getter
    private static IdempotentService idempotentService;

    @Autowired
    public void setStoredEventApplicationService(StoredEventApplicationService storedEventApplicationService) {
        CommonApplicationServiceRegistry.storedEventApplicationService = storedEventApplicationService;
    }
    @Autowired
    public void setChangeRecordApplicationService(ChangeRecordApplicationService idempotentApplicationService) {
        CommonApplicationServiceRegistry.changeRecordApplicationService = idempotentApplicationService;
    }
    @Autowired
    public void setIdempotentService(IdempotentService idempotentService) {
        CommonApplicationServiceRegistry.idempotentService = idempotentService;
    }

}
