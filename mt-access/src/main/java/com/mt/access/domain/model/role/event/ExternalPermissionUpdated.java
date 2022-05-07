package com.mt.access.domain.model.role.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalPermissionUpdated extends DomainEvent implements AuditEvent {
    public static final String EXTERNAL_PERMISSION_UPDATED = "external_permission_updated";
    public static final String name = "EXTERNAL_PERMISSION_UPDATED";
    @Getter
    private ProjectId projectId;

    public ExternalPermissionUpdated(ProjectId projectId) {
        super(projectId);
        setTopic(EXTERNAL_PERMISSION_UPDATED);
        setName(name);
        this.projectId = projectId;
    }
}
