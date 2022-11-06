package com.mt.proxy.infrastructure;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class GracefulShutDownHook {

    @PreDestroy
    public void onExit() {
        log.info("release resource before shutdown");
    }

    @PostConstruct
    public void checkLogConfig() {
        String property = System.getProperty("sun.java.command");
        if (property.contains(
            "-Dlog4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector")) {
            log.debug("currently async log is enabled, not all logs will flush when application shutdown");
        }
    }
}
