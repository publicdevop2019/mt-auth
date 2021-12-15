package com.mt.messenger.application.mall_notification;

import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.domain.DomainRegistry;
import com.mt.messenger.domain.model.mall_notification.MallNotification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MallNotificationApplicationService {

    public static final String MALL_NOTIFICATION = "MallNotification";

    @Transactional
    @SubscribeForEvent
    public void handle(MallNotificationEvent event) {
        ApplicationServiceRegistry.getIdempotentService().idempotent(event.getId().toString(), (command) -> {
                DomainRegistry.getMallNotificationService().create(event);
            return null;
        }, MALL_NOTIFICATION);
    }

    public SumPagedRep<MallNotification> notificationsOf(String pageParam, String skipCount) {
        return DomainRegistry.getMallNotificationRepository().latestMallNotifications(new PageConfig(pageParam, 200), new QueryConfig(skipCount));
    }
}
