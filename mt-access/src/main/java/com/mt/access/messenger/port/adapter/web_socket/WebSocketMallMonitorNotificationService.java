package com.mt.access.messenger.port.adapter.web_socket;

import com.mt.messenger.domain.service.MallMonitorNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WebSocketMallMonitorNotificationService implements MallMonitorNotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.MallMonitorHandler mallMonitorHandler;
    @Override
    public void notify(String message) {
        mallMonitorHandler.broadcast(message);
    }
}
