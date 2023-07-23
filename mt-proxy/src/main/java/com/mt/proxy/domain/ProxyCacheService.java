package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProxyCacheService {
    @Autowired
    LogService logService;
    @EventListener(ApplicationReadyEvent.class)
    public synchronized void reloadProxyCache() {
        logService.initTrace();
        log.info("start refresh cached endpoints");
        DomainRegistry.getEndpointService().refreshCache();
        DomainRegistry.getRegisteredApplicationService().refreshCache();
    }
}
