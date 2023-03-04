package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.endpoint.event.EndpointExpired.ENDPOINT_EXPIRED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.endpoint.event.EndpointExpired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SubRequestDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener0() {
        ListenerHelper.listen(ENDPOINT_EXPIRED, EndpointExpired.class,
            (event) -> ApplicationServiceRegistry.getSubRequestApplicationService()
                .handle(event));
    }
}
