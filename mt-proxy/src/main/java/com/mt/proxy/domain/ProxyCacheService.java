package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProxyCacheService {
    private volatile boolean reloadRequested = false;
    private volatile boolean reloadInProgress = false;

    public void triggerReload() {
        reloadRequested = true;
    }

    @Autowired
    LogService logService;

    @EventListener(ApplicationReadyEvent.class)
    public void onAppStartReload() {
        reloadRequested = true;
        reload();
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
    public void reload() {
        logService.initTrace();
        if (!reloadRequested) {
            log.info("refresh skipped due to not required");
            return;
        }
        if (reloadInProgress) {
            log.info("refresh skipped due to previous reload not finish");
            return;
        }
        reloadInProgress = true;
        log.info("start refresh cached endpoints");
        try {
            DomainRegistry.getEndpointService().refreshCache();
            DomainRegistry.getRegisteredApplicationService().refreshCache();
            reloadRequested = false;
        } catch (Exception ex) {
            log.error("exception during proxy refresh", ex);
        } finally {
            reloadInProgress = false;
        }
    }
}
