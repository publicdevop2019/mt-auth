package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.springcloudgateway.SCGRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class RegisteredApplicationService {
    @Autowired
    RetrieveRegisterApplicationService retrieveRegisterApplicationService;
    @Autowired
    SCGRouteService scgRouteService;
    Set<RegisteredApplication> cached;

    public void loadAll() {
        cached = retrieveRegisterApplicationService.fetchAll();
        log.debug("total registered application cached {}", cached.size());
        scgRouteService.refreshRoutes(cached);
    }
}
