package com.mt.access.application.notification;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.notification.Notification;
import com.mt.access.domain.model.notification.NotificationId;
import com.mt.access.domain.model.notification.NotificationQuery;
import com.mt.access.domain.model.notification.NotificationType;
import com.mt.access.domain.model.notification.event.SendBellNotification;
import com.mt.access.domain.model.notification.event.SendEmailNotification;
import com.mt.access.domain.model.notification.event.SendSmsNotification;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailed;
import com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning;
import com.mt.access.domain.model.sub_request.event.SubscribedEndpointExpired;
import com.mt.access.domain.model.user.event.MfaDeliverMethod;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.access.domain.model.user.event.UserMfaNotification;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceived;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceived;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.job.event.JobNotFound;
import com.mt.common.domain.model.job.event.JobPaused;
import com.mt.common.domain.model.job.event.JobStarving;
import com.mt.common.domain.model.job.event.JobThreadStarving;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Utility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationApplicationService {
    private static final String NOTIFICATION = "Notification";
    @Value("${mt.misc.instance-id}")
    private Long instanceId;

    public SumPagedRep<Notification> mgmtQueryBell(String queryParam, String pageParam,
                                                   String skipCount) {
        NotificationQuery notificationQuery =
            NotificationQuery.queryMgmtBell(queryParam, pageParam, skipCount);
        return DomainRegistry.getNotificationRepository()
            .notificationsOfQuery(notificationQuery);
    }

    public SumPagedRep<Notification> mgmtQuery(String pageParam,
                                               String skipConfig) {
        NotificationQuery notificationQuery =
            NotificationQuery.queryMgmt(pageParam, skipConfig);
        return DomainRegistry.getNotificationRepository()
            .notificationsOfQuery(notificationQuery);
    }

    public SumPagedRep<Notification> userQueryBell(String queryParam, String pageParam,
                                                   String skipCount) {
        NotificationQuery query =
            NotificationQuery.queryUserBell(queryParam, pageParam, skipCount,
                DomainRegistry.getCurrentUserService().getUserId());
        return DomainRegistry.getNotificationRepository()
            .notificationsOfQuery(query);
    }

    public void userAckBell(String id) {
        CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
            DomainRegistry.getNotificationRepository()
                .acknowledgeForUser(new NotificationId(id),
                    DomainRegistry.getCurrentUserService().getUserId());

        });
    }

    /**
     * acknowledge notification,
     * no idempotent wrapper required bcz itself is native idempotent.
     *
     * @param id notification id
     */
    public void ackBell(String id) {
        CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
            DomainRegistry.getNotificationRepository()
                .acknowledge(new NotificationId(id));
        });
    }

    /**
     * send bell notification to stored ws session
     * idempotent is for each instance
     *
     * @param event send bell notification event
     */
    public void handle(SendBellNotification event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                log.debug("sending bell notifications with {}, id {}", event.getTitle(),
                    event.getDomainId().getDomainId());
                if (event.getUserId() != null) {
                    DomainRegistry.getWsPushNotificationService()
                        .notifyUser(event.value(), event.getUserId());
                } else {
                    DomainRegistry.getWsPushNotificationService()
                        .notifyMgmt(event.value());
                }
                DomainRegistry.getNotificationRepository()
                    .markAsDelivered(new NotificationId(event.getDomainId().getDomainId()));
                return null;
            }, NOTIFICATION + "_" + instanceId);


    }

    /**
     * send sms to mobile
     *
     * @param event send bell notification event
     */
    public void handle(SendSmsNotification event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                DomainRegistry.getSmsNotificationService()
                    .notify(event.getMobile(), event.getCode());
                DomainRegistry.getNotificationRepository()
                    .markAsDelivered(new NotificationId(event.getDomainId().getDomainId()));
                return null;
            }, NOTIFICATION);

    }

    /**
     * send email notification
     *
     * @param event send email notification event
     */
    public void handle(SendEmailNotification event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                DomainRegistry.getEmailNotificationService()
                    .notify(event.getEmail(), event.getTemplateUrl(), event.getSubject(),
                        event.getParams());
                DomainRegistry.getNotificationRepository()
                    .markAsDelivered(new NotificationId(event.getDomainId().getDomainId()));
                return null;
            }, NOTIFICATION);
    }

    public void handle(HangingTxDetected event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(NewUserRegistered event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(ProjectOnboardingComplete event) {
        log.info("handle new project onboarding complete event, project id {}",
            event.getDomainId().getDomainId());
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(ProxyCacheCheckFailed event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(CrossDomainValidationService.ValidationFailed event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(UserMfaNotification event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                Notification notification = new Notification(event);
                DomainRegistry.getNotificationRepository().add(notification);
                if (MfaDeliverMethod.MOBILE.equals(event.getDeliverMethod())) {
                    context
                        .append(new SendSmsNotification(event, notification));
                } else {
                    context
                        .append(new SendEmailNotification(event, notification));
                }
                return null;
            }, NOTIFICATION);
    }

    public void handle(UserPwdResetCodeUpdated event) {
        Notification notification = new Notification(event);
        if (Utility.notNull(event.getEmail())) {
            SendEmailNotification sendEmailNotification =
                new SendEmailNotification(event, notification);
            storeEmailNotification(event.getId(), notification, sendEmailNotification);
        } else {
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(event.getId().toString(), (context) -> {
                    DomainRegistry.getNotificationRepository().add(notification);
                    context
                        .append(new SendSmsNotification(event, notification));
                    return null;
                }, NOTIFICATION);
        }
    }

    public void handle(VerificationCodeUpdated event) {
        Notification notification = new Notification(event);
        if (NotificationType.EMAIL.equals(notification.getType())) {
            SendEmailNotification sendEmailNotification =
                new SendEmailNotification(event, notification);
            storeEmailNotification(event.getId(), notification, sendEmailNotification);
        } else {
            CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(event.getId().toString(), (context) -> {
                    DomainRegistry.getNotificationRepository().add(notification);
                    context
                        .append(new SendSmsNotification(event, notification));
                    return null;
                }, NOTIFICATION);
        }
    }

    public void handle(CrossDomainValidationFailureCheck event) {
        Notification notification = new Notification(event);
        SendEmailNotification sendEmailNotification =
            new SendEmailNotification(event, notification);
        storeEmailNotification(event.getId(), notification, sendEmailNotification);
    }


    public void handle(UnrountableMsgReceived event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    /**
     * send bell notification to subscriber
     *
     * @param event SubscriberEndpointExpireEvent
     */
    public void handle(SubscribedEndpointExpired event) {
        event.getDomainIds().forEach(userId -> {
            Notification notification = new Notification(event, userId);
            storeBellNotification(event.getId() + "_" + userId.getDomainId(),
                notification);
        });
    }

    public void handle(JobPaused event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(JobNotFound event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(JobThreadStarving event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(JobStarving event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    public void handle(RejectedMsgReceived event) {
        if (!event.getSourceName().equalsIgnoreCase(SendBellNotification.name)) {
            //avoid infinite loop when send bell notification got rejected
            Notification notification = new Notification(event);
            storeBellNotification(event.getId().toString(), notification);
        }
    }

    public void handle(RawAccessRecordProcessingWarning event) {
        Notification notification = new Notification(event);
        storeBellNotification(event.getId().toString(), notification);
    }

    private void storeBellNotification(String uniqueId, Notification notification) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(uniqueId, (context) -> {
                DomainRegistry.getNotificationRepository().add(notification);
                context
                    .append(new SendBellNotification(notification));
                return null;
            }, NOTIFICATION);
    }

    private void storeEmailNotification(Long id, Notification notification,
                                        SendEmailNotification sendEmailNotification) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(id.toString(), (context) -> {
                DomainRegistry.getNotificationRepository().add(notification);
                context
                    .append(sendEmailNotification);
                return null;
            }, NOTIFICATION);
    }

}
