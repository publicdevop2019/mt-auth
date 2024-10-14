package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.notification.event.SendBellNotification.SEND_BELL_NOTIFICATION_EVENT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.notification.event.SendBellNotification;
import com.mt.access.domain.model.notification.event.SendEmailNotification;
import com.mt.access.domain.model.notification.event.SendSmsNotification;
import com.mt.access.domain.model.verification_code.event.VerificationCodeUpdated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailed;
import com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning;
import com.mt.access.domain.model.sub_request.event.SubscribedEndpointExpired;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.access.domain.model.user.event.UserMfaNotification;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceived;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceived;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.job.event.JobNotFound;
import com.mt.common.domain.model.job.event.JobPaused;
import com.mt.common.domain.model.job.event.JobStarving;
import com.mt.common.domain.model.job.event.JobThreadStarving;
import com.mt.common.infrastructure.RabbitMqEventStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    protected void hangingTxDetectedListener() {
        ListenerHelper.listen(new HangingTxDetected(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void newUserRegisteredListener() {
        ListenerHelper.listen(new NewUserRegistered(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void projectOnboardingCompleteListener() {
        ListenerHelper.listen(
            new ProjectOnboardingComplete(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void proxyCacheCheckFailedListener() {
        ListenerHelper.listen(new ProxyCacheCheckFailed(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void validationFailedListener() {
        ListenerHelper.listen(new CrossDomainValidationService.ValidationFailed(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void userMfaNotificationListener() {
        ListenerHelper.listen(new UserMfaNotification(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    /**
     * subscribe for bell notification event and broadcast to all instance.
     * this is due to websocket is stored separately in each instance,
     * and we need to send bell to all of them
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void sendBellNotificationListener() {
        ((RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService())
            .listen(AppInfo.MT_ACCESS_APP_ID, true, null, SendBellNotification.class,
                (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                    .handle(event), 20, SEND_BELL_NOTIFICATION_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void userPwdResetCodeUpdatedListener() {
        ListenerHelper.listen(new UserPwdResetCodeUpdated(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void verificationCodeUpdatedListener() {
        ListenerHelper.listen(new VerificationCodeUpdated(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void crossDomainValidationFailureCheckListener() {
        ListenerHelper.listen(new CrossDomainValidationFailureCheck(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void sendEmailNotificationListener() {
        ListenerHelper.listen(new SendEmailNotification(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }


    @EventListener(ApplicationReadyEvent.class)
    protected void sendSmsNotificationListener() {
        ListenerHelper.listen(new SendSmsNotification(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void unrountableMsgReceivedListener() {
        ListenerHelper.listen(new UnrountableMsgReceived(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void subscribedEndpointExpiredListener() {
        ListenerHelper.listen(new SubscribedEndpointExpired(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void jobPausedListener() {
        ListenerHelper.listen(new JobPaused(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void jobNotFoundListener() {
        ListenerHelper.listen(new JobNotFound(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void jobThreadStarvingListener() {
        ListenerHelper.listen(new JobThreadStarving(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }


    /**
     * handle rejected msg.
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void rejectedMsgReceivedListener() {
        ListenerHelper.listen(new RejectedMsgReceived(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void rawAccessRecordProcessingWarningListener() {
        ListenerHelper.listen(new RawAccessRecordProcessingWarning(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void jobStarvingListener() {
        ListenerHelper.listen(new JobStarving(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }
}
