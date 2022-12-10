package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.spring_cloud_gateway.ScgRouteService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegisteredApplicationService {
    @Autowired
    RetrieveRegisterApplicationService retrieveRegisterApplicationService;
    @Autowired
    ScgRouteService scgRouteService;
    Set<RegisteredApplication> cached;

    public void refreshCache() {
        cached = retrieveRegisterApplicationService.fetchAll();
        log.debug("total registered application cached {}", cached.size());
        scgRouteService.refreshRoutes(cached);
    }
}
