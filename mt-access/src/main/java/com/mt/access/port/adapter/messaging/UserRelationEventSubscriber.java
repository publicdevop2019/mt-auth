package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class UserRelationEventSubscriber {
    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(new NewProjectRoleCreated(),
            (event) -> ApplicationServiceRegistry.getUserRelationApplicationService()
                .handle(event));
    }
}
