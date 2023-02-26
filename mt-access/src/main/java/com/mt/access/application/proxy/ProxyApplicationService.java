package com.mt.access.application.proxy;

import static com.mt.access.infrastructure.AppConstant.PROXY_VALIDATION_JOB_NAME;

import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProxyApplicationService {

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 180 * 1000)
    protected void checkSum() {
        log.trace("triggered scheduled task 2");
        CommonDomainRegistry.getJobService()
            .execute(PROXY_VALIDATION_JOB_NAME,
                () -> CommonDomainRegistry.getTransactionService().transactional(() -> {
                    log.debug("[checking proxy cache value] started");
                    DomainRegistry.getProxyService().checkSum();
                    log.debug("[checking proxy cache value] completed");
                }));
    }

    public CheckSumRepresentation checkSumValue() {
        return DomainRegistry.getProxyService().checkSumValue();
    }
}
