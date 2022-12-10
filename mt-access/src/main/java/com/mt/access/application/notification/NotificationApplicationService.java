package com.mt.access.application.notification;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationId;
import com.mt.access.domain.model.notification.NotificationQuery;
import com.mt.access.domain.model.notification.NotificationType;
import com.mt.access.domain.model.notification.event.SendBellNotificationEvent;
import com.mt.access.domain.model.notification.event.SendEmailNotificationEvent;
import com.mt.access.domain.model.notification.event.SendSmsNotificationEvent;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.sub_request.event.SubscriberEndpointExpireEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class NotificationApplicationService {
    public static final String NOTIFICATION = "Notification";

    public SumPagedRep<Notification> bellNotificationsOf(String queryParam, String pageParam,
                                                         String skipCount) {
        NotificationQuery notificationQuery =
            new NotificationQuery(NotificationType.BELL, queryParam, pageParam, skipCount);
        return DomainRegistry.getNotificationRepository()
            .notificationsOfQuery(notificationQuery);
    }

    public SumPagedRep<Notification> notificationsOf(String queryParam, String pageParam,
                                                     String skipCount) {
        NotificationQuery notificationQuery =
            new NotificationQuery(queryParam, pageParam, skipCount);
        return DomainRegistry.getNotificationRepository()
            .notificationsOfQuery(notificationQuery);
    }

    @Transactional
    public void handle(HangingTxDetected event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(NewUserRegistered event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(ProjectOnboardingComplete event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(ProxyCacheCheckFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(CrossDomainValidationService.ValidationFailedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(UserMfaNotificationEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendSmsNotificationEvent(event, notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(UserPwdResetCodeUpdated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendEmailNotificationEvent(event, notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(PendingUserActivationCodeUpdated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendEmailNotificationEvent(event, notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(CrossDomainValidationFailureCheck event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendEmailNotificationEvent(event, notification));
                return null;
            }, NOTIFICATION);
    }

    /**
     * send bell notification to admin sessions
     *
     * @param event send bell notification event
     */
    @Transactional
    public void handle(SendBellNotificationEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("sending bell notifications with {}", event.getTitle());
                DomainRegistry.getWsPushNotificationService()
                    .notify(event.value());
                try {
                    DomainRegistry.getNotificationRepository()
                        .notificationOfId(new NotificationId(event.getDomainId().getDomainId()))
                        .ifPresent(
                            Notification::markAsDelivered);
                } catch (Exception ex) {
                    log.warn(
                        "ignore exception when trying to update same notification entity");
                }
                return null;
            }, NOTIFICATION);

    }

    /**
     * send sms to mobile
     *
     * @param event send bell notification event
     */
    @Transactional
    public void handle(SendSmsNotificationEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                DomainRegistry.getSmsNotificationService()
                    .notify(event.getMobile(), event.getCode());
                DomainRegistry.getNotificationRepository()
                    .notificationOfId(new NotificationId(event.getDomainId().getDomainId()))
                    .ifPresent(
                        Notification::markAsDelivered);
                return null;
            }, NOTIFICATION);

    }

    /**
     * send email notification
     *
     * @param event send email notification event
     */
    @Transactional
    public void handle(SendEmailNotificationEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                DomainRegistry.getEmailNotificationService()
                    .notify(event.getEmail(), event.getTemplateUrl(), event.getSubject(),
                        event.getParams());
                DomainRegistry.getNotificationRepository()
                    .notificationOfId(new NotificationId(event.getDomainId().getDomainId()))
                    .ifPresent(
                        Notification::markAsDelivered);
                return null;
            }, NOTIFICATION);
    }

    /**
     * acknowledge notification,
     * no idempotent wrapper required bcz itself is native idempotent.
     *
     * @param id notification id
     */
    @Transactional
    public void ackBellNotification(String id) {
        DomainRegistry.getNotificationRepository()
            .acknowledge(new NotificationId(id));
    }

    @Transactional
    public void handle(UnrountableMsgReceivedEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }

    @Transactional
    public void handle(SubscriberEndpointExpireEvent event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (command) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                CommonDomainRegistry.getDomainEventRepository()
                    .append(new SendBellNotificationEvent(notification));
                return null;
            }, NOTIFICATION);
    }
}
