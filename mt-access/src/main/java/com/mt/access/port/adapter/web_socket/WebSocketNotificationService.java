package com.mt.access.port.adapter.web_socket;

import com.mt.access.domain.model.notification.WsPushNotificationService;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.job.JobDetail;
import com.mt.common.infrastructure.CleanUpThreadPoolExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketNotificationService implements WsPushNotificationService {
    @Autowired
    SpringBootSimpleWebSocketConfig.NotificationWsHandler notificationWsHandler;
    @Autowired
    CleanUpThreadPoolExecutor taskExecutor;

    @Override
    public void notify(String message) {
        notificationWsHandler.broadcast(message);
    }

    @Scheduled(fixedRate = 25 * 1000)
    protected void autoRenew() {
        taskExecutor.execute(() -> {
            log.debug("start of renewing ws connects");
            notificationWsHandler.broadcast("_renew");
            CommonApplicationServiceRegistry.getJobApplicationService()
                .createOrUpdateJob(JobDetail.wsRenew());
            log.debug("end of renewing ws connects");
        });
    }
}
