package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.client.event.ClientAsResourceDeleted.CLIENT_AS_RESOURCE_DELETED;
import static com.mt.access.domain.model.role.event.ExternalPermissionUpdated.EXTERNAL_PERMISSION_UPDATED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ClientDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_AS_RESOURCE_DELETED, (event) -> {
                ClientAsResourceDeleted deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientAsResourceDeleted.class);
                ApplicationServiceRegistry.getClientApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, EXTERNAL_PERMISSION_UPDATED, (event) -> {
                ExternalPermissionUpdated deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ExternalPermissionUpdated.class);
                ApplicationServiceRegistry.getClientApplicationService().handle(deserialize);
            });
    }

}
