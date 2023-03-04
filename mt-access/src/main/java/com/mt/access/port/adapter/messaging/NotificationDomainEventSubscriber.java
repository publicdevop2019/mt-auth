package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.CrossDomainValidationService.ValidationFailedEvent.SYSTEM_VALIDATION_FAILED;
import static com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck.CROSS_DOMAIN_VALIDATION_FAILURE_CHECK;
import static com.mt.access.domain.model.notification.event.SendBellNotificationEvent.SEND_BELL_NOTIFICATION_EVENT;
import static com.mt.access.domain.model.notification.event.SendEmailNotificationEvent.SEND_EMAIL_NOTIFICATION_EVENT;
import static com.mt.access.domain.model.notification.event.SendSmsNotificationEvent.SEND_SMS_NOTIFICATION_EVENT;
import static com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated.PENDING_USER_ACTIVATION_CODE_UPDATED;
import static com.mt.access.domain.model.pending_user.event.PendingUserCreated.PENDING_USER_CREATED;
import static com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent.PROXY_CACHE_CHECK_FAILED_EVENT;
import static com.mt.access.domain.model.report.event.RawAccessRecordProcessingWarning.RAW_ACCESS_RECORD_PROCESSING_WARNING;
import static com.mt.access.domain.model.sub_request.event.SubscriberEndpointExpireEvent.SUBSCRIBER_ENDPOINT_EXPIRE;
import static com.mt.access.domain.model.user.event.NewUserRegistered.USER_CREATED;
import static com.mt.access.domain.model.user.event.UserMfaNotificationEvent.USER_MFA_NOTIFICATION;
import static com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated.USER_PWD_RESET_CODE_UPDATED;
import static com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete.PROJECT_ONBOARDING_COMPLETED;
import static com.mt.common.domain.model.domain_event.event.RejectedMsgReceivedEvent.REJECTED_MSG_EVENT;
import static com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent.UNROUTABLE_MSG_EVENT;
import static com.mt.common.domain.model.idempotent.event.HangingTxDetected.HANGING_TX_DETECTED;
import static com.mt.common.domain.model.job.event.JobNotFoundEvent.JOB_NOT_FOUND;
import static com.mt.common.domain.model.job.event.JobPausedEvent.JOB_PAUSED;
import static com.mt.common.domain.model.job.event.JobStarvingEvent.JOB_STARVING;

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
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.event.RejectedMsgReceivedEvent;
import com.mt.common.domain.model.domain_event.event.UnrountableMsgReceivedEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import com.mt.common.domain.model.job.event.JobNotFoundEvent;
import com.mt.common.domain.model.job.event.JobPausedEvent;
import com.mt.common.domain.model.job.event.JobStarvingEvent;
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
        ListenerHelper.listen(HANGING_TX_DETECTED, HangingTxDetected.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener1() {
        ListenerHelper.listen(USER_CREATED, NewUserRegistered.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener2() {
        ListenerHelper.listen(
            PROJECT_ONBOARDING_COMPLETED, ProjectOnboardingComplete.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener3() {
        ListenerHelper.listen(PROXY_CACHE_CHECK_FAILED_EVENT,
            ProxyCacheCheckFailedEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener4() {
        ListenerHelper.listen(SYSTEM_VALIDATION_FAILED,
            CrossDomainValidationService.ValidationFailedEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener5() {
        ListenerHelper.listen(USER_MFA_NOTIFICATION,
            UserMfaNotificationEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    /**
     * subscribe for bell notification event for all instance.
     * this is due to websocket is stored separately in each instance
     * and we need to send bell to all of them
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void listener6() {
        ((RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService())
            .listen(AppInfo.MT_ACCESS_APP_ID, true, null, SendBellNotificationEvent.class,
                (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                    .handle(event), SEND_BELL_NOTIFICATION_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener7() {
        ListenerHelper.listen(USER_PWD_RESET_CODE_UPDATED,
            UserPwdResetCodeUpdated.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener8() {
        ListenerHelper.listen(PENDING_USER_ACTIVATION_CODE_UPDATED,
            PendingUserActivationCodeUpdated.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener9() {
        ListenerHelper.listen(CROSS_DOMAIN_VALIDATION_FAILURE_CHECK,
            CrossDomainValidationFailureCheck.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener11() {
        ListenerHelper.listen(SEND_EMAIL_NOTIFICATION_EVENT,
            SendEmailNotificationEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }


    @EventListener(ApplicationReadyEvent.class)
    protected void listener12() {
        ListenerHelper.listen(SEND_SMS_NOTIFICATION_EVENT,
            SendSmsNotificationEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener13() {
        ListenerHelper.listen(UNROUTABLE_MSG_EVENT,
            UnrountableMsgReceivedEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener14() {
        ListenerHelper.listen(SUBSCRIBER_ENDPOINT_EXPIRE,
            SubscriberEndpointExpireEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener15() {
        ListenerHelper.listen(JOB_PAUSED, JobPausedEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener16() {
        ListenerHelper.listen(JOB_NOT_FOUND, JobNotFoundEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener17() {
        ListenerHelper.listen(JOB_STARVING, JobStarvingEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }


    @EventListener(ApplicationReadyEvent.class)
    protected void listener18() {
        ListenerHelper.listen(PENDING_USER_CREATED, PendingUserCreated.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener19() {
        ListenerHelper.listen(REJECTED_MSG_EVENT, RejectedMsgReceivedEvent.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener20() {
        ListenerHelper.listen(RAW_ACCESS_RECORD_PROCESSING_WARNING,
            RawAccessRecordProcessingWarning.class,
            (event) -> ApplicationServiceRegistry.getNotificationApplicationService()
                .handle(event));
    }


}
