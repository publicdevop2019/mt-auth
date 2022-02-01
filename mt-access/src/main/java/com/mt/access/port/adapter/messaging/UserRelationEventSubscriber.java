package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.mt.access.domain.model.role.event.NewProjectRoleCreated.NEW_PROJECT_ROLE_CREATED;


@Slf4j
@Component
public class UserRelationEventSubscriber {
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        CommonDomainRegistry.getEventStreamService().of(appName, true, NEW_PROJECT_ROLE_CREATED, (event) -> {
            NewProjectRoleCreated deserialize = CommonDomainRegistry.getCustomObjectSerializer().deserialize(event.getEventBody(), NewProjectRoleCreated.class);
            ApplicationServiceRegistry.getUserRelationApplicationService().handle(deserialize);
        });
    }
}
