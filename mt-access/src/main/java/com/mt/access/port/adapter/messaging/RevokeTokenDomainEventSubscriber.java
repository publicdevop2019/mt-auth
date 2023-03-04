package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.client.event.ClientDeleted.CLIENT_DELETED;

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
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RevokeTokenDomainEventSubscriber {

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        ListenerHelper.listen(new ClientResourceCleanUpCompleted(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener3() {
        ListenerHelper.listen(new UserAuthorityChanged(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener4() {
        ListenerHelper.listen(new UserDeleted(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener5() {
        ListenerHelper.listen(new UserGetLocked(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener6() {
        ListenerHelper.listen(new UserPasswordChanged(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener7() {
        ListenerHelper.listen(new ClientAccessibilityRemoved(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener9() {
        ListenerHelper.listen(new ClientGrantTypeChanged(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener10() {
        ListenerHelper.listen(new ClientTokenDetailChanged(),
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
        ListenerHelper.listen(new ClientResourcesChanged(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }


    @EventListener(ApplicationReadyEvent.class)
    private void listener14() {
        ListenerHelper.listen(new ClientSecretChanged(),
            (event) -> ApplicationServiceRegistry.getRevokeTokenApplicationService()
                .handleChange(event));
    }

}
