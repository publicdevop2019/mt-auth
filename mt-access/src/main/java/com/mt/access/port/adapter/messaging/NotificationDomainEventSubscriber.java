package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.CrossDomainValidationService.ValidationFailedEvent.SYSTEM_VALIDATION_FAILED;
import static com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent.PROXY_CACHE_CHECK_FAILED_EVENT;
import static com.mt.access.domain.model.user.event.NewUserRegistered.USER_CREATED;
import static com.mt.access.domain.model.user.event.UserMfaNotificationEvent.USER_MFA_NOTIFICATION;
import static com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete.PROJECT_ONBOARDING_COMPLETED;
import static com.mt.common.domain.model.idempotent.event.HangingTxDetected.MONITOR_TOPIC;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.CrossDomainValidationService;
import com.mt.access.domain.model.proxy.event.ProxyCacheCheckFailedEvent;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.access.domain.model.user.event.UserMfaNotificationEvent;
import com.mt.access.domain.model.user_relation.event.ProjectOnboardingComplete;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
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
        CommonDomainRegistry.getEventStreamService().subscribe(AppInfo.MT_ACCESS_APP_ID, true,
            null, (event) -> {
                ProjectOnboardingComplete deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), ProjectOnboardingComplete.class);
                ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
            }, PROJECT_ONBOARDING_COMPLETED);
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
                        .deserialize(event.getEventBody(), CrossDomainValidationService.ValidationFailedEvent.class);
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

}
