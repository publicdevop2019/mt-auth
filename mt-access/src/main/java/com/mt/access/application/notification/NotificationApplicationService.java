package com.mt.access.application.notification;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.notification.representation.NotificationWebSocketRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationId;
import com.mt.access.domain.model.notification.NotificationQuery;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.PageConfig;
import com.mt.common.domain.model.restful.query.QueryConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationApplicationService {

    public static final String NOTIFICATION = "Notification";

    public SumPagedRep<Notification> notificationsOf(String queryParam, String pageParam,
                                                     String skipCount) {
        NotificationQuery notificationQuery =
            new NotificationQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getNotificationRepository()
            .latestNotifications(notificationQuery);
    }

    @Transactional
    public void handle(HangingTxDetected event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                DomainRegistry.getWsPushNotificationService()
                    .notify(new NotificationWebSocketRepresentation(notification).value());
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(NewUserRegistered event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                DomainRegistry.getWsPushNotificationService()
                    .notify(new NotificationWebSocketRepresentation(notification).value());
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(ProjectOnboardingComplete event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(ProxyCacheCheckFailedEvent event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                DomainRegistry.getWsPushNotificationService()
                    .notify(new NotificationWebSocketRepresentation(notification).value());
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(CrossDomainValidationService.ValidationFailedEvent event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                DomainRegistry.getWsPushNotificationService()
                    .notify(new NotificationWebSocketRepresentation(notification).value());
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(UserMfaNotificationEvent event) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                DomainRegistry.getSmsNotificationService()
                    .notify(event.getMobile(), event.getCode().getValue());
                return null;
            }, NOTIFICATION);
    }

    /**
     * trigger websocket notification to all session.
     *
     * @param event project complete event
     */
    public void notify(ProjectOnboardingComplete event) {
        DomainRegistry.getWsPushNotificationService()
            .notify(new NotificationWebSocketRepresentation(new Notification(event)).value());
    }

    /**
     * acknowledge notification,
     * no idempotent wrapper required bcz itself is native idempotent.
     *
     * @param id notification id
     */
    @Transactional
    public void acknowledge(String id) {
        DomainRegistry.getNotificationRepository()
            .acknowledge(new NotificationId(id));
    }
}
