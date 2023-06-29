package com.mt.access.domain.model.audit;

import static com.mt.access.domain.model.audit.AuditLogAspectConfig.AUDIT_PREFIX;

import com.mt.access.domain.DomainRegistry;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
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
                log.error("error during audit event scan", e);
            }
        }
    }

    public SumPagedRep<StoredEvent> getAuditEvent(String queryParam, String pageParam,
                                                  String skipCount) {
        return CommonApplicationServiceRegistry.getStoredEventApplicationService()
            .query(auditEventNames, queryParam, pageParam, skipCount);
    }

    /**
     * log action for audit without user login info
     *
     * @param logger         logger
     * @param userIdentifier user identifier
     * @param action         action detail
     */
    public void logExternalUserAction(Logger logger, String userIdentifier, String action) {
        logger.info("{} user: {} action : {}", AUDIT_PREFIX, userIdentifier, action);
    }

    /**
     * log action for audit with user id
     *
     * @param logger logger
     * @param action action name
     * @param detail action detail
     */
    public void logUserAction(Logger logger, String action, Object detail) {
        logger.info("{} user: {} action : {} detail: {}", AUDIT_PREFIX,
            DomainRegistry.getCurrentUserService().getUserId()
                .getDomainId(), action,
            CommonDomainRegistry.getCustomObjectSerializer().serialize(detail));
    }

    public void storeAuditAction(String action, Object detail) {
        AuditRecord auditRecord = new AuditRecord(
            action,
            DomainRegistry.getCurrentUserService().getUserId().getDomainId(),
            CommonDomainRegistry.getCustomObjectSerializer().serialize(detail)
        );
        DomainRegistry.getAuditRepository().add(auditRecord);
    }
}
