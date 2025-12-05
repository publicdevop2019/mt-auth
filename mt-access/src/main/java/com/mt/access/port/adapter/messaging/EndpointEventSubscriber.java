package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EndpointEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener17() {
        ListenerHelper.listen(new CorsProfileRemoved(),
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener18() {
        ListenerHelper.listen(new CorsProfileUpdated(),
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener21() {
        ListenerHelper.listen(new CacheProfileUpdated(),
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener22() {
        ListenerHelper.listen(new CacheProfileRemoved(),
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }
}
