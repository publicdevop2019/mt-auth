package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.notification.event.SendBellNotificationEvent.SEND_BELL_NOTIFICATION_EVENT;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.notification.event.SendBellNotificationEvent;
import com.mt.access.domain.model.notification.event.SendEmailNotificationEvent;
import com.mt.access.domain.model.notification.event.SendSmsNotificationEvent;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.pending_user.event.PendingUserCreated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning;
import com.mt.access.domain.model.sub_request.event.SubscriberEndpointExpireEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.ProjectOnboardingComplete;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceivedEvent;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.domain.model.job.event.JobPausedEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
import com.mt.common.domain.model.job.event.JobThreadStarvingEvent;
import com.mt.common.infrastructure.RabbitMqEventStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    protected void listener0() {
        ListenerHelper.listen(new HangingTxDetected(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener1() {
        ListenerHelper.listen(new NewUserRegistered(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener2() {
        ListenerHelper.listen(
            new ProjectOnboardingComplete(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener3() {
        ListenerHelper.listen(new ProxyCacheCheckFailedEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener4() {
        ListenerHelper.listen(new CrossDomainValidationService.ValidationFailedEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener5() {
        ListenerHelper.listen(new UserMfaNotificationEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    /**
     * subscribe for bell notification event and broadcast to all instance.
     * this is due to websocket is stored separately in each instance,
     * and we need to send bell to all of them
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void listener6() {
        ((RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService())
            .listen(AppInfo.MT_ACCESS_APP_ID, true, null, SendBellNotificationEvent.class,
                (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                    .handle(event), 20, SEND_BELL_NOTIFICATION_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener7() {
        ListenerHelper.listen(new UserPwdResetCodeUpdated(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener8() {
        ListenerHelper.listen(new PendingUserActivationCodeUpdated(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener9() {
        ListenerHelper.listen(new CrossDomainValidationFailureCheck(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener11() {
        ListenerHelper.listen(new SendEmailNotificationEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }


    @EventListener(ApplicationReadyEvent.class)
    protected void listener12() {
        ListenerHelper.listen(new SendSmsNotificationEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener13() {
        ListenerHelper.listen(new UnrountableMsgReceivedEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener14() {
        ListenerHelper.listen(new SubscriberEndpointExpireEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener15() {
        ListenerHelper.listen(new JobPausedEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener16() {
        ListenerHelper.listen(new JobNotFoundEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener17() {
        ListenerHelper.listen(new JobThreadStarvingEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }


    @EventListener(ApplicationReadyEvent.class)
    protected void listener18() {
        ListenerHelper.listen(new PendingUserCreated(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event), 10);
    }

    /**
     * handle rejected msg.
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void listener19() {
        ListenerHelper.listen(new RejectedMsgReceivedEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener20() {
        ListenerHelper.listen(new RawAccessRecordProcessingWarning(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener21() {
        ListenerHelper.listen(new JobStarvingEvent(),
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }
}
