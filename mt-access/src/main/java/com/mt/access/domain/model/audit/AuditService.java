package com.mt.access.domain.model.audit;

import static com.mt.access.domain.model.audit.AuditLogAspectConfig.AUDIT_PREFIX;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditService {

    public void logUserAction(String userEmail, String action, String detail) {
        log.info("{} user: {} action : {} detail: {}", AUDIT_PREFIX, userEmail, action, detail);
    }

    public void logUserAction(String userEmail, String action) {
        log.info("{} user: {} action : {}", AUDIT_PREFIX, userEmail, action);
    }

    public void logCurrentUserAction(String action, String detail) {
        log.info("{} user: {} action : {} detail: {}", AUDIT_PREFIX,
            DomainRegistry.getCurrentUserService().getUserId()
                .getDomainId(), action, detail);
    }

    public void logAdminAction(UserId user, String action, String detail) {
        log.info("{} admin: {} action : {} detail: {}", AUDIT_PREFIX, user.getDomainId(), action,
            detail);
    }

    public void logSystemAction(String action) {
        log.info("{} system execute action : {}", AUDIT_PREFIX, action);
    }
}
