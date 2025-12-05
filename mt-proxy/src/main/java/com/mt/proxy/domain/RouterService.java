package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.filter.ScgRouteService;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouterService {
    @Autowired
    private RetrieveRouterService retrieveRouterService;
    @Autowired
    private ScgRouteService scgRouteService;
    private Set<Router> cached;

    public void refreshCache() {
        cached = retrieveRouterService.fetchAll();
        log.info("total router cached {}", cached.size());
        scgRouteService.refreshRoutes(cached);
    }
}
