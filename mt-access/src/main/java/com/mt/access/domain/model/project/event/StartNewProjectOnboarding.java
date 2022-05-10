package com.mt.access.domain.model.project.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class StartNewProjectOnboarding extends DomainEvent {
    public static final String START_NEW_PROJECT_ONBOARDING = "start_new_project_onboarding";
    public static final String name = "START_NEW_PROJECT_ONBOARDING";
    @Getter
    private UserId creator;
    @Getter
    private String projectName;

    public StartNewProjectOnboarding(Project project) {
        super(project.getProjectId());
        setTopic(START_NEW_PROJECT_ONBOARDING);
        setName(name);
        projectName = project.getName();
        this.creator = new UserId(project.getCreatedBy());
    }
}
