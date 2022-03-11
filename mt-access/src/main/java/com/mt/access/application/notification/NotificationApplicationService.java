package com.mt.access.application.notification;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.notification.representation.NotificationRepresentation;
import com.mt.access.application.notification.representation.NotificationWebSocketRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationApplicationService {

    public static final String NOTIFICATION = "notification";

    @Transactional
    @SubscribeForEvent
    public void handle(HangingTxDetected event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(event.getId().toString(), (command) -> {
            Notification notification = new Notification(event);
            DomainRegistry.getNotificationRepository().add(notification);
            DomainRegistry.getNotificationService().notify(new NotificationWebSocketRepresentation(notification).value());
            return null;
        }, NOTIFICATION);
    }

    public SumPagedRep<Notification> notificationsOf(String pageParam, String skipCount) {
        return DomainRegistry.getNotificationRepository().latestNotifications(PageConfig.limited(pageParam, 200), new QueryConfig(skipCount));
    }
    @Transactional
    @SubscribeForEvent
    public void handle(NewUserRegistered event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(event.getId().toString(), (command) -> {
            Notification notification = new Notification(event);
            DomainRegistry.getNotificationRepository().add(notification);
            DomainRegistry.getNotificationService().notify(new NotificationWebSocketRepresentation(notification).value());
            return null;
        }, NOTIFICATION);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(ProjectCreated event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(event.getId().toString(), (command) -> {
            Notification notification = new Notification(event);
            DomainRegistry.getNotificationRepository().add(notification);
            DomainRegistry.getNotificationService().notify(new NotificationWebSocketRepresentation(notification).value());
            return null;
        }, NOTIFICATION);
    }
    @Transactional
    @SubscribeForEvent
    public void handle(ProxyCacheCheckFailedEvent event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(event.getId().toString(), (command) -> {
            Notification notification = new Notification(event);
            DomainRegistry.getNotificationRepository().add(notification);
            DomainRegistry.getNotificationService().notify(new NotificationWebSocketRepresentation(notification).value());
            return null;
        }, NOTIFICATION);
    }
}
