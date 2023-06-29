package com.mt.proxy.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProxyCacheService {

    @EventListener(ApplicationReadyEvent.class)
    public synchronized void reloadProxyCache() {
        DomainRegistry.getEndpointService().refreshCache();
        DomainRegistry.getRegisteredApplicationService().refreshCache();
    }
}
