package com.mt.messenger.domain.service;

import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import com.mt.messenger.domain.model.system_notification.SystemNotificationId;
import org.springframework.stereotype.Service;

@Service
public class SystemNotificationService {
    public SystemNotificationId create(HangingTxDetected deserialize) {
        SystemNotification systemNotification = new SystemNotification(deserialize);
        DomainRegistry.getSystemNotificationRepository().add(systemNotification);
        DomainRegistry.getSystemMonitorNotificationService().notify(systemNotification.getDetails());
        return systemNotification.getSystemNotificationId();
    }
}
