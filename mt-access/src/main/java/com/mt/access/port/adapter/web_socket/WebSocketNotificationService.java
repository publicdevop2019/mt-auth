package com.mt.access.port.adapter.web_socket;

import com.mt.access.domain.model.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketNotificationService implements NotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.NotificationWSHandler notificationWSHandler;
    @Override
    public void notify(String message) {
        notificationWSHandler.broadcast(message);
    }
}
