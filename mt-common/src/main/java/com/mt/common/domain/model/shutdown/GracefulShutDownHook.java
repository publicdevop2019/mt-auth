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
        String property = System.getProperty("log4j2.contextSelector");
        if (property!=null && !property.isBlank()) {
            log.info("async log is enabled");
        }else{
            log.info("async log is not enabled");
        }
    }
}
