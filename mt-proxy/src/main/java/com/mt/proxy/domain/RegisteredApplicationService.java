package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.springcloudgateway.SCGRouteService;
import com.mt.proxy.port.adapter.http.RegisterApplicationAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
@Slf4j
@Service
public class RegisteredApplicationService {
    @Autowired
    RegisterApplicationAdapter registerApplicationAdapter;
    @Autowired
    SCGRouteService scgRouteService;
    Set<RegisteredApplication> cached;
    @EventListener(ApplicationReadyEvent.class)
    public void loadAll() {
        cached = registerApplicationAdapter.fetchAll();
        log.debug("total registered application cached {}",cached.size());
        scgRouteService.refreshRoutes(cached);
    }
}
