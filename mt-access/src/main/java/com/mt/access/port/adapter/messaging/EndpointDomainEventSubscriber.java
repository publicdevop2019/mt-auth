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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EndpointDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CLIENT_DELETED, (event) -> {
            ClientDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                .deserialize(event.getEventBody(), ClientDeleted.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener17() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CORS_PROFILE_REMOVED, (event) -> {
                CorsProfileRemoved deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), CorsProfileRemoved.class);
                ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener18() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CORS_PROFILE_UPDATED, (event) -> {
                CorsProfileUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), CorsProfileUpdated.class);
                ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener21() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CACHE_PROFILE_UPDATED, (event) -> {
                CacheProfileUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), CacheProfileUpdated.class);
                ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener22() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CACHE_PROFILE_REMOVED, (event) -> {
                CacheProfileRemoved deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), CacheProfileRemoved.class);
                ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
            });
    }
}
