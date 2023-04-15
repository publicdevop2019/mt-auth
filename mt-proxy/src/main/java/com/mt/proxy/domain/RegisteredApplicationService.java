package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.filter.ScgRouteService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RegisteredApplicationService {
    @Autowired
    private RetrieveRegisterApplicationService retrieveRegisterApplicationService;
    @Autowired
    private ScgRouteService scgRouteService;
    private Set<RegisteredApplication> cached;

    public void refreshCache() {
        cached = retrieveRegisterApplicationService.fetchAll();
        log.info("total registered application cached {}", cached.size());
        scgRouteService.refreshRoutes(cached);
    }
}
