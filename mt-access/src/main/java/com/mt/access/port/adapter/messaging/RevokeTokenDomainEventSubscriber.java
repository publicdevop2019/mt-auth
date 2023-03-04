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
import com.mt.common.domain.model.constant.AppInfo;
import com.mt.common.domain.model.domain_event.MqHelper;
import com.mt.common.infrastructure.RabbitMqEventStreamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RevokeTokenDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        ListenerHelper.listen(CLIENT_RESOURCE_CLEAN_UP_COMPLETED,
            ClientResourceCleanUpCompleted.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        ListenerHelper.listen(USER_AUTHORITY_CHANGED, UserAuthorityChanged.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        ListenerHelper.listen(USER_DELETED, UserDeleted.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        ListenerHelper.listen(USER_GET_LOCKED, UserGetLocked.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        ListenerHelper.listen(USER_GET_LOCKED, UserPasswordChanged.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        ListenerHelper.listen(CLIENT_ACCESSIBILITY_REMOVED,
            ClientAccessibilityRemoved.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        ListenerHelper.listen(CLIENT_GRANT_TYPE_CHANGED,
            ClientGrantTypeChanged.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener10() {
        ListenerHelper.listen(CLIENT_TOKEN_DETAIL_CHANGED,
            ClientTokenDetailChanged.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener11() {
        ((RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService())
            .listen(AppInfo.MT_ACCESS_APP_ID, true,
                MqHelper.handlerOf(AppInfo.MT_ACCESS_APP_ID + "_token", CLIENT_DELETED),
                ClientDeleted.class,
                (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                    .handleChange(event), CLIENT_DELETED);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener12() {
        ListenerHelper.listen(CLIENT_RESOURCES_CHANGED,
            ClientResourcesChanged.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener14() {
        ListenerHelper.listen(
            CLIENT_SECRET_CHANGED, ClientSecretChanged.class,
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

}
