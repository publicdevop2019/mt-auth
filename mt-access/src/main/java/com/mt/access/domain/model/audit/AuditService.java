package com.mt.access.domain.model.audit;

import static com.mt.access.domain.model.audit.AuditLogAspectConfig.AUDIT_PREFIX;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuditService {
    private static final String EVENT_NAME = "name";
    private final Set<String> auditEventNames = new HashSet<>();

    @PostConstruct
    public void scanAuditEvent() {
        log.debug("scanning audit event in package");
        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(AuditEvent.class));
        for (BeanDefinition bd : scanner.findCandidateComponents("com.mt")) {
            Class<?> clazz;
            try {
                clazz = Class.forName(bd.getBeanClassName());
                Field name = clazz.getField(EVENT_NAME);
                auditEventNames.add(String.valueOf(name.get(null)));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public SumPagedRep<StoredEvent> getAuditEvent(String queryParam, String pageParam,
                                                  String skipCount) {
        return CommonApplicationServiceRegistry.getStoredEventApplicationService()
            .query(auditEventNames, queryParam, pageParam, skipCount);
    }

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
