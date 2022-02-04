package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.system_role.event.SystemRoleDeleted;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.MQHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.system_role.event.SystemRoleDeleted.SYSTEM_ROLE_DELETED;

@Slf4j
@Component
public class DomainEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener16() {
        CommonDomainRegistry.getEventStreamService().subscribe(appName, true, MQHelper.handlerOf(appName + "_user", SYSTEM_ROLE_DELETED), (event) -> {
            SystemRoleDeleted deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), SystemRoleDeleted.class);
            ApplicationServiceRegistry.getUserApplicationService().handleChange(deserialize);
        }, SYSTEM_ROLE_DELETED);
    }

}
