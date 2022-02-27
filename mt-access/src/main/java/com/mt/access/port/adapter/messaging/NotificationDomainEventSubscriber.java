package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.user.command.UserCreateCommand;
import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.access.domain.model.user.event.NewUserRegistered;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.idempotent.event.HangingTxDetected;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.project.event.ProjectCreated.PROJECT_CREATED;
import static com.mt.access.domain.model.user.event.NewUserRegistered.USER_CREATED;
import static com.mt.common.domain.model.idempotent.event.HangingTxDetected.MONITOR_TOPIC;

@Slf4j
@Component
public class NotificationDomainEventSubscriber {

    private static final String MESSENGER_SYS_MONITOR_QUEUE = "messenger_sys_monitor_queue";

    @EventListener(ApplicationReadyEvent.class)
    protected void listener0() {
        CommonDomainRegistry.getEventStreamService().subscribe(AppInfo.MT_ACCESS_APP_ID, false, MESSENGER_SYS_MONITOR_QUEUE, (event) -> {
            HangingTxDetected deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), HangingTxDetected.class);
            ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
        }, MONITOR_TOPIC);
    }
    @EventListener(ApplicationReadyEvent.class)
    protected void listener1() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT_ACCESS_APP_ID, true, USER_CREATED, (event) -> {
            NewUserRegistered deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), NewUserRegistered.class);
            ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
        });
    }
    @EventListener(ApplicationReadyEvent.class)
    protected void listener2() {
        CommonDomainRegistry.getEventStreamService().of(AppInfo.MT_ACCESS_APP_ID, true, PROJECT_CREATED, (event) -> {
            ProjectCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ProjectCreated.class);
            ApplicationServiceRegistry.getNotificationApplicationService().handle(deserialize);
        });
    }

}
