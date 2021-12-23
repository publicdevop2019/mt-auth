package com.mt.proxy.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProxyCacheService {
    @Autowired
    EndpointService endpointService;
    @Autowired
    RegisteredApplicationService registeredApplicationService;

    @EventListener(ApplicationReadyEvent.class)
    public void reloadProxyCache() {
        endpointService.loadAllEndpoints();
        registeredApplicationService.loadAll();
    }
}
