package com.mt.common.domain.model.shutdown;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.infrastructure.RabbitMqEventStreamService;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
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
}
