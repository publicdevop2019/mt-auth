package com.mt.access.port.adapter.web_socket;

import com.mt.messenger.domain.service.SystemMonitorNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketSystemMonitorNotificationService implements SystemMonitorNotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.SystemMonitorHandler systemMonitorHandler;
    @Override
    public void notify(String message) {
        systemMonitorHandler.broadcast(message);
    }
}
