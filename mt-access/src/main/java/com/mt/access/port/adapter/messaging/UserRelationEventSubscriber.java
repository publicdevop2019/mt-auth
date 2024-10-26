package com.mt.access.port.adapter.messaging;

import static com.mt.access.domain.model.user.event.UserDeleted.USER_DELETED;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.domain.model.user.event.UserDeleted;
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
public class UserRelationEventSubscriber {
    @EventListener(ApplicationReadyEvent.class)
    private void listener1() {
        ListenerHelper.listen(new NewProjectRoleCreated(),
            (event) -> ApplicationServiceRegistry.getUserRelationApplicationService()
                .handle(event), 10);
    }

    @EventListener(ApplicationReadyEvent.class)
    private void listener2() {
        ((RabbitMqEventStreamService) CommonDomainRegistry.getEventStreamService())
            .listen(AppInfo.MT_ACCESS_APP_ID, true,
                MqHelper.handlerOf(AppInfo.MT_ACCESS_APP_ID + "_user_relation", USER_DELETED),
                UserDeleted.class,
                (event) -> ApplicationServiceRegistry.getUserRelationApplicationService()
                    .handle(event), 1, USER_DELETED);
    }

}
