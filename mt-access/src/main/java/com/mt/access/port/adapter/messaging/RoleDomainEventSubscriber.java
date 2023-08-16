package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.client.event.ClientDeleted.CLIENT_DELETED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.permission.event.PermissionRemoved;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.infrastructure.RabbitMqEventStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RoleDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        ListenerHelper.listen(new ProjectPermissionCreated(),
            (event) -> ApplicationServiceRegistry.getRoleApplicationService().handle(event), 10);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(new ClientCreated(),
            (event) -> ApplicationServiceRegistry.getRoleApplicationService().handle(event), 10);
    }

    /**
     * client deleted role handler.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        ((RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService())
            .listen(AppInfo.MT_ACCESS_APP_ID, true, "role_" + CLIENT_DELETED + "_handler",
                ClientDeleted.class,
                (event) -> ApplicationServiceRegistry.getRoleApplicationService().handle(event),
                CLIENT_DELETED);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        ListenerHelper.listen(new PermissionRemoved(),
            (event) -> ApplicationServiceRegistry.getRoleApplicationService().handle(event));
    }
}
