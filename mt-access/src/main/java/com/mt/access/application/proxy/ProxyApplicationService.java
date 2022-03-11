package com.mt.access.application.proxy;

import com.mt.access.application.proxy.representation.CheckSumRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProxyApplicationService {
    @Scheduled(fixedRate = 60 * 1000, initialDelay = 180 * 1000)
    @Transactional
    @SubscribeForEvent
    protected void checkSum() {
        log.debug("start of checking proxy cache value");
        DomainRegistry.getProxyService().checkSum();
    }

    public CheckSumRepresentation checkSumValue() {
        return DomainRegistry.getProxyService().checkSumValue();
    }
}
