package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved;
import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        ListenerHelper.listen(new StartNewProjectOnboarding(),
            (event) -> ApplicationServiceRegistry.getPermissionApplicationService().handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(new SecureEndpointCreated(),
            (event) -> ApplicationServiceRegistry.getPermissionApplicationService()
                .handle(event));
    }

    /**
     * secured endpoint deleted permission handler.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        ListenerHelper.listen(new SecureEndpointRemoved(),
            (event) -> ApplicationServiceRegistry.getPermissionApplicationService()
                .handle(event));
    }
}
