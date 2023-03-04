package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.endpoint.event.SecureEndpointCreated.SECURE_ENDPOINT_CREATED;
import static com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved.SECURE_ENDPOINT_REMOVED;
import static com.mt.access.domain.model.project.event.StartNewProjectOnboarding.START_NEW_PROJECT_ONBOARDING;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved;
import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        ListenerHelper.listen(
            START_NEW_PROJECT_ONBOARDING, StartNewProjectOnboarding.class,
            (event) -> ApplicationServiceRegistry.getPermissionApplicationService().handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen( SECURE_ENDPOINT_CREATED,
                SecureEndpointCreated.class,
                (event) -> ApplicationServiceRegistry.getPermissionApplicationService()
                    .handle(event));
    }

    /**
     * secured endpoint deleted permission handler.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        ListenerHelper.listen( SECURE_ENDPOINT_REMOVED,
                SecureEndpointRemoved.class,
                (event) -> ApplicationServiceRegistry.getPermissionApplicationService()
                    .handle(event));
    }
}
