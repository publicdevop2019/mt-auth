package com.mt.messenger.port.adapter.messaging;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.event.MallNotificationEvent;
import com.mt.messenger.application.ApplicationServiceRegistry;
import com.mt.messenger.domain.model.email_delivery.event.PendingUserActivationCodeUpdated;
import com.mt.messenger.domain.model.email_delivery.event.UserPwdResetCodeUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.common.domain.model.idempotent.event.HangingTxDetected.MONITOR_TOPIC;

@Slf4j
@Component
public class MessageQueueListener {
    private static final String MESSENGER_SYS_MONITOR_QUEUE = "messenger_sys_monitor_queue";

    @EventListener(ApplicationReadyEvent.class)
    protected void listener0() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT_ACCESS_APP_ID, false, "user_pwd_reset_code_updated", (event) -> {
            UserPwdResetCodeUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), UserPwdResetCodeUpdated.class);
            ApplicationServiceRegistry.getEmailDeliverApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    protected void listener1() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT_ACCESS_APP_ID, false, "pending_user_activation_code_updated", (event) -> {
            PendingUserActivationCodeUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), PendingUserActivationCodeUpdated.class);
            ApplicationServiceRegistry.getEmailDeliverApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void systemMonitorListener() {
        CommonDomainRegistry.getEventStreamService().subscribe(AppInfo.MT_ACCESS_APP_ID, false, MESSENGER_SYS_MONITOR_QUEUE, (event) -> {
            ApplicationServiceRegistry.getSystemNotificationApplicationService().handleMonitorEvent(event);
        }, MONITOR_TOPIC);
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void mallMonitorListener() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT3_APP_NAME, false, AppInfo.EventName.MT3_MALL_NOTIFICATION, (event) -> {
            MallNotificationEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), MallNotificationEvent.class);
            ApplicationServiceRegistry.getMallNotificationApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    protected void mallMonitorListener2() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT2_APP_NAME, false, AppInfo.EventName.MT3_MALL_NOTIFICATION, (event) -> {
            MallNotificationEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), MallNotificationEvent.class);
            ApplicationServiceRegistry.getMallNotificationApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    protected void mallMonitorListener3() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT6_APP_NAME, false, AppInfo.EventName.MT3_MALL_NOTIFICATION, (event) -> {
            MallNotificationEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), MallNotificationEvent.class);
            ApplicationServiceRegistry.getMallNotificationApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    protected void mallMonitorListener4() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT15_APP_NAME, false, AppInfo.EventName.MT3_MALL_NOTIFICATION, (event) -> {
            MallNotificationEvent deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), MallNotificationEvent.class);
            ApplicationServiceRegistry.getMallNotificationApplicationService().handle(deserialize);
        });
    }
}
