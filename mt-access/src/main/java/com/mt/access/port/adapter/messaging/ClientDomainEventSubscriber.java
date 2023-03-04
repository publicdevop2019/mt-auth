package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.client.event.ClientAsResourceDeleted.CLIENT_AS_RESOURCE_DELETED;
import static com.mt.access.domain.model.role.event.ExternalPermissionUpdated.EXTERNAL_PERMISSION_UPDATED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientDomainEventSubscriber {
    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        ListenerHelper.listen(CLIENT_AS_RESOURCE_DELETED, ClientAsResourceDeleted.class,
            (event) -> ApplicationServiceRegistry.getClientApplicationService().handle(event)
        );
//        ListenerHelper.listen2(new ClientAsResourceDeleted(),
//            (event) -> ApplicationServiceRegistry.getClientApplicationService().handle(event)
//        );
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(EXTERNAL_PERMISSION_UPDATED, ExternalPermissionUpdated.class,
            (event) -> ApplicationServiceRegistry.getClientApplicationService().handle(event));
    }

}
