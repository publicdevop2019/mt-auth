package com.mt.access.port.adapter.messaging;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import com.mt.common.domain.CommonDomainRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmailDeliveryDomainEventSubscriber {
    private static final String USER_PWD_RESET_CODE_UPDATED = "user_pwd_reset_code_updated";
    private static final String PENDING_USER_ACTIVATION_CODE_UPDATED =
        "pending_user_activation_code_updated";
    @Value("${spring.application.name}")
    private String appName;

    @EventListener(ApplicationReadyEvent.class)
    protected void listener0() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, false, USER_PWD_RESET_CODE_UPDATED, (event) -> {
                UserPwdResetCodeUpdated deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), UserPwdResetCodeUpdated.class);
                ApplicationServiceRegistry.getEmailDeliverApplicationService().handle(deserialize);
            });
    }

    @EventListener(ApplicationReadyEvent.class)
    protected void listener1() {
        CommonDomainRegistry.getEventStreamService()
            .of(appName, false, PENDING_USER_ACTIVATION_CODE_UPDATED, (event) -> {
                PendingUserActivationCodeUpdated deserialize =
                    CommonDomainRegistry.getCustomObjectSerializer()
                        .deserialize(event.getEventBody(), PendingUserActivationCodeUpdated.class);
                ApplicationServiceRegistry.getEmailDeliverApplicationService().handle(deserialize);
            });
    }


}
