package com.mt.access.port.adapter.web_socket;

import static com.mt.access.infrastructure.AppConstant.KEEP_WS_CONNECTION_JOB_NAME;

import com.mt.access.domain.model.notification.WsPushNotificationService;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebSocketNotificationService implements WsPushNotificationService {

    @Autowired
    NotificationMgmtWsHandler mgmtWsHandler;
    @Autowired
    NotificationUserWsHandler userWsHandler;

    @Override
    public void notifyMgmt(String message) {
        mgmtWsHandler.broadcastToAll(message);
    }

    @Override
    public void notifyUser(String message, UserId userId) {
        userWsHandler.notifyUser(userId, message);
    }

    @Scheduled(fixedRate = 25 * 1000)
    protected void autoRenew() {
        CommonDomainRegistry.getLogService().init();
        CommonDomainRegistry.getJobService()
            .execute(KEEP_WS_CONNECTION_JOB_NAME, (ignored) -> {
                log.trace("start of renewing all ws connects");
                mgmtWsHandler.broadcastToAll("_renew");
                userWsHandler.broadcastToAll("_renew");
                log.trace("end of renewing all ws connects");
            }, false);
    }
}
