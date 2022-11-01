package com.mt.common.domain.model.shutdown;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.infrastructure.RabbitMqEventStreamService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GracefulShutDownHook {

    @PreDestroy
    public void onExit() {
        log.info("release resource before shutdown");
        if (CommonDomainRegistry.getEventStreamService() instanceof RabbitMqEventStreamService) {
            RabbitMqEventStreamService eventStreamService =
                (RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService();
            eventStreamService.releaseResource();
        }
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
