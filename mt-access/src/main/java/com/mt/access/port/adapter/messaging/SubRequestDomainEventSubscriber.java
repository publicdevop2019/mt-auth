package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.client.event.ClientAsResourceDeleted.CLIENT_AS_RESOURCE_DELETED;
import static com.mt.access.domain.model.endpoint.event.EndpointExpired.ENDPOINT_EXPIRED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.endpoint.event.EndpointExpired;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubRequestDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, ENDPOINT_EXPIRED, (event) -> {
                EndpointExpired deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), EndpointExpired.class);
                ApplicationServiceRegistry.getSubRequestApplicationService().handle(deserialize);
            });
    }
}
