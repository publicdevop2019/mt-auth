package com.mt.access.port.adapter.web_socket;

import com.mt.access.domain.model.NotificationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Slf4j
@Component
public class WebSocketNotificationService implements NotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.NotificationWSHandler notificationWSHandler;
    @Override
    public void notify(String message) {
        notificationWSHandler.broadcast(message);
    }

    @Override
    public void renew() {
        notificationWSHandler.broadcast("_renew");
    }
    @Scheduled(fixedRate = 25 * 1000)
    protected void autoRenew(){
        log.debug("start of renewing ws connects");
        renew();
    }
}
