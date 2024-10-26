package com.mt.proxy.infrastructure;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GracefulShutDownHook {
    @PostConstruct
    public void checkLogConfig() {
        String property = System.getProperty("log4j2.contextSelector");
        if (property != null && !property.isBlank()) {
            log.debug("asynchronous log is enabled");
        } else {
            log.debug("asynchronous log is not enabled");
        }
    }
}
