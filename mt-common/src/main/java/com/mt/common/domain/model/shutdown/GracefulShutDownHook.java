package com.mt.common.domain.model.shutdown;

import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GracefulShutDownHook {

    @PreDestroy
    public void onExit() {
        log.info("release resource before shutdown");
    }
}
