package com.mt.access.application.pending_user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.activation_code.ActivationCode;
import com.mt.access.domain.model.operation_cool_down.OperationType;
import com.mt.access.domain.model.pending_user.RegistrationEmail;
import com.mt.common.application.CommonApplicationServiceRegistry;
import org.springframework.stereotype.Service;

@Service
public class PendingUserApplicationService {

    private static final String PENDING_USER = "PendingUser";

    public String create(PendingUserCreateCommand command, String changeId) {
        RegistrationEmail registrationEmail = new RegistrationEmail(command.getEmail());
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    DomainRegistry.getCoolDownService().hasCoolDown(registrationEmail.getDomainId(),
                        OperationType.PENDING_USER_CODE);
                    RegistrationEmail orUpdatePendingUser = DomainRegistry.getPendingUserService()
                        .createOrUpdatePendingUser(registrationEmail, new ActivationCode(),context);
                    return orUpdatePendingUser.getDomainId();
                }, PENDING_USER
            );
    }
}
