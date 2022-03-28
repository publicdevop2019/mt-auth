package com.mt.proxy.infrastructure;

import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GracefulShutDownHook {
    @PreDestroy
    public void onExit() {
        log.info("Closing application..");
        LogManager.shutdown();
    }
}
