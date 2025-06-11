package com.mt.proxy.domain;

import com.mt.proxy.port.adapter.messaging.MqListener;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.SmartLifecycle;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InstanceService implements SmartLifecycle {
    @Autowired
    private InstanceInfo instanceInfo;
    @Autowired
    private InstanceIdService instanceIdService;
    @Autowired
    private ProxyCacheService proxyCacheService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private MqListener mqListener;

    public void init() {
        instanceIdService.iniInstanceId("mt-proxy");
        if (instanceInfo.getId() == null) {
            return;
        }
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletableFuture.runAsync(() -> proxyCacheService.initialReload(), executorService);
        CompletableFuture.runAsync(() -> jwtService.loadKeys(), executorService);
        CompletableFuture.runAsync(() -> mqListener.init(), executorService);
    }

    @Scheduled(fixedRate = 15 * 1000, initialDelay = 30 * 1000)
    public void retryFailed() {
        if (!instanceInfo.ready()) {
            if (instanceInfo.getAutoServiceIn()) {
                log.info("retry failed init");
                init();
            } else {
                log.info("auto service-in disabled, pls manually enable it");
            }
        }
    }

    /**
     * renew instance id
     */
    @Scheduled(fixedRate = 60 * 1000, initialDelay = 60 * 1000)
    public void scheduledRenew() {
        if (instanceInfo.getId() != null) {
            instanceIdService.renew();
        } else {
            log.debug("skipped instance id renew due to instance id not ready");
        }
    }

    @Override
    public void start() {
        log.info("on start init");
        instanceInfo.setRunning(true);
        if (instanceInfo.getAutoServiceIn()) {
            init();
        } else {
            log.info("auto service-in disabled, pls manually enable it");
        }
    }

    @Override
    public void stop() {
        log.info("stopping app");
        instanceIdService.removeInstanceId();
        instanceInfo.setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return instanceInfo.getRunning();
    }

}
