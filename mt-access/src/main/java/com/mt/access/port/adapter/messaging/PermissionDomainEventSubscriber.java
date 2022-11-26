package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.endpoint.event.EndpointShareAdded.ENDPOINT_SHARED_ADDED;
import static com.mt.access.domain.model.endpoint.event.SecureEndpointCreated.SECURE_ENDPOINT_CREATED;
import static com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved.SECURE_ENDPOINT_REMOVED;
import static com.mt.access.domain.model.project.event.StartNewProjectOnboarding.START_NEW_PROJECT_ONBOARDING;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.event.EndpointShareAdded;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved;
import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PermissionDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService().of(appName, true,
            START_NEW_PROJECT_ONBOARDING, (event) -> {
                StartNewProjectOnboarding deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), StartNewProjectOnboarding.class);
                ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, SECURE_ENDPOINT_CREATED, (event) -> {
                SecureEndpointCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), SecureEndpointCreated.class);
                ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, ENDPOINT_SHARED_ADDED, (event) -> {
                EndpointShareAdded deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), EndpointShareAdded.class);
                ApplicationServiceRegistry.getPermissionApplicationService().handle(deserialize);
            });
    }

    /**
     * secured endpoint deleted permission handler.
     */
    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService()
            .subscribe(appName, true, "permission_" + SECURE_ENDPOINT_REMOVED + "_handler",
                (event) -> {
                    SecureEndpointRemoved deserialize =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .deserialize(event.getEventBody(), SecureEndpointRemoved.class);
                    ApplicationServiceRegistry.getPermissionApplicationService()
                        .handle(deserialize);
                }, SECURE_ENDPOINT_REMOVED);
    }
}
