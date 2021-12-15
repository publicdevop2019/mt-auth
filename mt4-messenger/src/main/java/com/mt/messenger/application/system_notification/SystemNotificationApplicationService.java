package com.mt.messenger.application.system_notification;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.system_notification.SystemNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SystemNotificationApplicationService {

    public static final String SYSTEM_NOTIFICATION = "SystemNotification";

    @Transactional
    @SubscribeForEvent
    public void handleMonitorEvent(StoredEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (command) -> {
            if (event.getName().equals(HangingTxDetected.class.getName())) {
                HangingTxDetected deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), HangingTxDetected.class);
                DomainRegistry.getSystemNotificationService().create(deserialize);
            }
            return null;
        }, SYSTEM_NOTIFICATION);
    }

    public SumPagedRep<SystemNotification> notificationsOf(String pageParam, String skipCount) {
        return DomainRegistry.getSystemNotificationRepository().latestSystemNotifications(new PageConfig(pageParam, 200), new QueryConfig(skipCount));
    }
}
