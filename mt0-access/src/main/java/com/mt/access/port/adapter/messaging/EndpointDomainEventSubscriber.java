package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.cors_profile.event.CORSProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CORSProfileUpdated;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved.CACHE_PROFILE_REMOVED;
import static com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated.CACHE_PROFILE_UPDATED;
import static com.mt.access.domain.model.client.event.ClientDeleted.CLIENT_DELETED;
import static com.mt.access.domain.model.cors_profile.event.CORSProfileRemoved.CORS_PROFILE_REMOVED;
import static com.mt.access.domain.model.cors_profile.event.CORSProfileUpdated.CORS_PROFILE_UPDATED;
import static com.mt.access.domain.model.system_role.event.SystemRoleDeleted.SYSTEM_ROLE_DELETED;

@Slf4j
@Component
public class EndpointDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CLIENT_DELETED, (event) -> {
            ClientDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), ClientDeleted.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener17() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CORS_PROFILE_REMOVED, (event) -> {
            CORSProfileRemoved deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CORSProfileRemoved.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener18() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CORS_PROFILE_UPDATED, (event) -> {
            CORSProfileUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CORSProfileUpdated.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener20() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, SYSTEM_ROLE_DELETED, (event) -> {
            SystemRoleDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), SystemRoleDeleted.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener21() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CACHE_PROFILE_UPDATED, (event) -> {
            CacheProfileUpdated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CacheProfileUpdated.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener22() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, CACHE_PROFILE_REMOVED, (event) -> {
            CacheProfileRemoved deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), CacheProfileRemoved.class);
            ApplicationServiceRegistry.getEndpointApplicationService().handle(deserialize);
        });
    }
}
