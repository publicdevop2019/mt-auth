package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.CrossDomainValidationService.ValidationFailedEvent.SYSTEM_VALIDATION_FAILED;
import static com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck.CROSS_DOMAIN_VALIDATION_FAILURE_CHECK;
import static com.mt.access.domain.model.notification.event.SendBellNotificationEvent.SEND_BELL_NOTIFICATION_EVENT;
import static com.mt.access.domain.model.notification.event.SendEmailNotificationEvent.SEND_EMAIL_NOTIFICATION_EVENT;
import static com.mt.access.domain.model.notification.event.SendSmsNotificationEvent.SEND_SMS_NOTIFICATION_EVENT;
import static com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated.PENDING_USER_ACTIVATION_CODE_UPDATED;
import static com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent.PROXY_CACHE_CHECK_FAILED_EVENT;
import static com.mt.access.domain.model.user.event.NewUserRegistered.USER_CREATED;
import static com.mt.access.domain.model.user.event.UserMfaNotificationEvent.USER_MFA_NOTIFICATION;
import static com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated.USER_PWD_RESET_CODE_UPDATED;
import static com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete.PROJECT_ONBOARDING_COMPLETED;
import static com.mt.common.domain.model.domain_event.UnrountableMessageEvent.UNROUTABLE_MSG_EVENT;
import static com.mt.common.domain.model.idempotent.event.HangingTxDetected.MONITOR_TOPIC;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.notification.event.SendBellNotificationEvent;
import com.mt.access.domain.model.notification.event.SendEmailNotificationEvent;
import com.mt.access.domain.model.notification.event.SendSmsNotificationEvent;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.UnrountableMessageEvent;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NotificationDomainEventSubscriber {

    private static final String MESSENGER_SYS_MONITOR_QUEUE = "messenger_sys_monitor_queue";

    @EventListener(ApplicationReadyEvent.class)
    protected void listener0() {
        CommonDomainRegistry.getEventStreamService()
            .subscribe(AppInfo.MT_ACCESS_APP_ID, false, MESSENGER_SYS_MONITOR_QUEUE, (event) -> {
                HangingTxDetected deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), HangingTxDetected.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            }, MONITOR_TOPIC);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener1() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, USER_CREATED, (event) -> {
                NewUserRegistered deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), NewUserRegistered.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener2() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT_ACCESS_APP_ID, true,
            PROJECT_ONBOARDING_COMPLETED, (event) -> {
                ProjectOnboardingComplete deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ProjectOnboardingComplete.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener3() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, PROXY_CACHE_CHECK_FAILED_EVENT, (event) -> {
                ProxyCacheCheckFailedEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ProxyCacheCheckFailedEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener4() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, SYSTEM_VALIDATION_FAILED, (event) -> {
                CrossDomainValidationService.ValidationFailedEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(),
                            CrossDomainValidationService.ValidationFailedEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener5() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, USER_MFA_NOTIFICATION, (event) -> {
                UserMfaNotificationEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), UserMfaNotificationEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    /**
     * subscribe for bell notification event for all instance.
     * this is due to websocket is stored separately in each instance
     * and we need to send bell to all of them
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void listener6() {
        CommonDomainRegistry.getEventStreamService()
            .subscribe(AppInfo.MT_ACCESS_APP_ID, true, null, (event) -> {
                SendBellNotificationEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), SendBellNotificationEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            }, SEND_BELL_NOTIFICATION_EVENT);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener7() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, false, USER_PWD_RESET_CODE_UPDATED, (event) -> {
                UserPwdResetCodeUpdated deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), UserPwdResetCodeUpdated.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener8() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, false, PENDING_USER_ACTIVATION_CODE_UPDATED, (event) -> {
                PendingUserActivationCodeUpdated deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), PendingUserActivationCodeUpdated.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener9() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, CROSS_DOMAIN_VALIDATION_FAILURE_CHECK, (event) -> {
                CrossDomainValidationFailureCheck deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), CrossDomainValidationFailureCheck.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener11() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, SEND_EMAIL_NOTIFICATION_EVENT, (event) -> {
                SendEmailNotificationEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), SendEmailNotificationEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }


    @EventListener(ApplicationReadyEvent.class)
    protected void listener12() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, SEND_SMS_NOTIFICATION_EVENT, (event) -> {
                SendSmsNotificationEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), SendSmsNotificationEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener13() {
        CommonDomainRegistry.getEventStreamService()
            .of(AppInfo.MT_ACCESS_APP_ID, true, UNROUTABLE_MSG_EVENT, (event) -> {
                UnrountableMessageEvent deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), UnrountableMessageEvent.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            });
    }


}
