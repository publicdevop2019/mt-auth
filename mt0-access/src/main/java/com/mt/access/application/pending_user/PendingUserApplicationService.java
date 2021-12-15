package com.mt.access.application.pending_user;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PendingUserApplicationService {
    @SubscribeForEvent
    @Transactional
    public String create(PendingUserCreateCommand command, String operationId) {
        RegistrationEmail registrationEmail = new RegistrationEmail(command.getEmail());
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(operationId,
                (change) -> {
                    RegistrationEmail orUpdatePendingUser = DomainRegistry.getPendingUserService().createOrUpdatePendingUser(registrationEmail, new ActivationCode());
                    return orUpdatePendingUser.getDomainId();
                }, "PendingUser"
        );
    }
}
