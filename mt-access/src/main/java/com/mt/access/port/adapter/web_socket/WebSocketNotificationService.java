package com.mt.access.port.adapter.web_socket;

import com.mt.access.domain.model.NotificationService;
import com.mt.common.infrastructure.CleanUpThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketNotificationService implements NotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.NotificationWSHandler notificationWSHandler;
    @Autowired
    CleanUpThreadPoolExecutor taskExecutor;

    @Override
    public void notify(String message) {
        notificationWSHandler.broadcast(message);
    }

    @Scheduled(fixedRate = 25 * 1000)
    protected void autoRenew() {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                log.debug("start of renewing ws connects");
                notificationWSHandler.broadcast("_renew");
            }
        });
    }
}
