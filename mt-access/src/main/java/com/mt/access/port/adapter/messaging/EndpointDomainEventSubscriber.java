package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved.CACHE_PROFILE_REMOVED;
import static com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated.CACHE_PROFILE_UPDATED;
import static com.mt.access.domain.model.client.event.ClientDeleted.CLIENT_DELETED;
import static com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved.CORS_PROFILE_REMOVED;
import static com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated.CORS_PROFILE_UPDATED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.constant.AppInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EndpointDomainEventSubscriber {
    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(CLIENT_DELETED, ClientDeleted.class,
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener17() {
        ListenerHelper.listen(CORS_PROFILE_REMOVED, CorsProfileRemoved.class,
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener18() {
        ListenerHelper.listen(CORS_PROFILE_UPDATED, CorsProfileUpdated.class,
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener21() {
        ListenerHelper.listen(CACHE_PROFILE_UPDATED, CacheProfileUpdated.class,
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener22() {
        ListenerHelper.listen(CACHE_PROFILE_REMOVED, CacheProfileRemoved.class,
            (event) -> ApplicationServiceRegistry.getEndpointApplicationService()
                .handle(event));
    }
}
