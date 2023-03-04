package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientDomainEventSubscriber {
    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        ListenerHelper.listen(new ClientAsResourceDeleted(),
            (event) -> ApplicationServiceRegistry.getClientApplicationService().handle(event)
        );
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(new ExternalPermissionUpdated(),
            (event) -> ApplicationServiceRegistry.getClientApplicationService().handle(event));
    }

}
