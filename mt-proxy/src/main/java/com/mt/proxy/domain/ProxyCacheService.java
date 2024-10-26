package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.LogService;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProxyCacheService {
    public static final String CACHE_LOG_PREFIX = "cache-sync";
    @Autowired
    LogService logService;
    private volatile Long reloadRequestedAt = 0L;
    private volatile Long completedReloadRequestAt = 0L;
    private volatile boolean reloadInProgressLock = false;

    public void triggerReload() {
        reloadRequestedAt = Instant.now().toEpochMilli();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onAppStartReload() {
        reloadRequestedAt = Instant.now().toEpochMilli();
        reload();
    }

    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
    public void reload() {
        logService.initTrace();
        log.info("{} start cache refresh check", CACHE_LOG_PREFIX);
        if (reloadInProgressLock) {
            log.warn("{} refresh skipped due to previous reload not finish", CACHE_LOG_PREFIX);
            return;
        }
        if (reloadRequestedAt.equals(completedReloadRequestAt)) {
            log.info("{} refresh skipped due to not required", CACHE_LOG_PREFIX);
            return;
        }
        reloadInProgressLock = true;
        final long nextCompletedReloadRequestAt =
            reloadRequestedAt;//copy value, make sure it will not change
        log.info("{} start refresh cached endpoints", CACHE_LOG_PREFIX);
        try {
            DomainRegistry.getEndpointService().refreshCache();
            DomainRegistry.getRegisteredApplicationService().refreshCache();
            completedReloadRequestAt = nextCompletedReloadRequestAt;
            log.info("{} refresh cached endpoints end", CACHE_LOG_PREFIX);
        } catch (Exception ex) {
            log.error("{} exception during proxy refresh", CACHE_LOG_PREFIX, ex);
        } finally {
            reloadInProgressLock = false;
        }
    }
}
