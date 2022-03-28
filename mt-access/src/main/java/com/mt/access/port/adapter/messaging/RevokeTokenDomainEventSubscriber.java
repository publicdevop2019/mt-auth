package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.client.event.ClientAccessibilityRemoved.CLIENT_ACCESSIBILITY_REMOVED;
import static com.mt.access.domain.model.client.event.ClientDeleted.CLIENT_DELETED;
import static com.mt.access.domain.model.client.event.ClientGrantTypeChanged.CLIENT_GRANT_TYPE_CHANGED;
import static com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted.CLIENT_RESOURCE_CLEAN_UP_COMPLETED;
import static com.mt.access.domain.model.client.event.ClientResourcesChanged.CLIENT_RESOURCES_CHANGED;
import static com.mt.access.domain.model.client.event.ClientSecretChanged.CLIENT_SECRET_CHANGED;
import static com.mt.access.domain.model.client.event.ClientTokenDetailChanged.CLIENT_TOKEN_DETAIL_CHANGED;
import static com.mt.access.domain.model.user.event.UserAuthorityChanged.USER_AUTHORITY_CHANGED;
import static com.mt.access.domain.model.user.event.UserDeleted.USER_DELETED;
import static com.mt.access.domain.model.user.event.UserGetLocked.USER_GET_LOCKED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.client.event.ClientAccessibilityRemoved;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.client.event.ClientGrantTypeChanged;
import com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted;
import com.mt.access.domain.model.client.event.ClientResourcesChanged;
import com.mt.access.domain.model.client.event.ClientSecretChanged;
import com.mt.access.domain.model.client.event.ClientTokenDetailChanged;
import com.mt.access.domain.model.user.event.UserAuthorityChanged;
import com.mt.access.domain.model.user.event.UserDeleted;
import com.mt.access.domain.model.user.event.UserGetLocked;
import com.mt.access.domain.model.user.event.UserPasswordChanged;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.MQHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RevokeTokenDomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_RESOURCE_CLEAN_UP_COMPLETED, (event) -> {
                ClientResourceCleanUpCompleted deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientResourceCleanUpCompleted.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, USER_AUTHORITY_CHANGED, (event) -> {
                UserAuthorityChanged deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), UserAuthorityChanged.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, USER_DELETED, (event) -> {
            UserDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                .deserialize(event.getEventBody(), UserDeleted.class);
            ApplicationServiceRegistry.getRevokeTokenApplicationService().handleChange(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, USER_GET_LOCKED, (event) -> {
            UserGetLocked deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                .deserialize(event.getEventBody(), UserGetLocked.class);
            ApplicationServiceRegistry.getRevokeTokenApplicationService().handleChange(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, USER_GET_LOCKED, (event) -> {
            UserPasswordChanged deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                .deserialize(event.getEventBody(), UserPasswordChanged.class);
            ApplicationServiceRegistry.getRevokeTokenApplicationService().handleChange(deserialize);
        });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_ACCESSIBILITY_REMOVED, (event) -> {
                ClientAccessibilityRemoved deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientAccessibilityRemoved.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_GRANT_TYPE_CHANGED, (event) -> {
                ClientGrantTypeChanged deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientGrantTypeChanged.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener10() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_TOKEN_DETAIL_CHANGED, (event) -> {
                ClientTokenDetailChanged deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientTokenDetailChanged.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener11() {
        CommonDomainRegistry.getEventStreamService()
            .subscribe(appName, true, MQHelper.handlerOf(appName + "_token", CLIENT_DELETED),
                (event) -> {
                    ClientDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientDeleted.class);
                    ApplicationServiceRegistry.getRevokeTokenApplicationService()
                        .handleChange(deserialize);
                }, CLIENT_DELETED);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener12() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_RESOURCES_CHANGED, (event) -> {
                ClientResourcesChanged deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), ClientResourcesChanged.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener14() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, true, CLIENT_SECRET_CHANGED, (event) -> {
                ClientSecretChanged deserialize = CommonDomainRegistry.getCustomObjectSerializer()
                    .deserialize(event.getEventBody(), ClientSecretChanged.class);
                ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(deserialize);
            });
    }

}
