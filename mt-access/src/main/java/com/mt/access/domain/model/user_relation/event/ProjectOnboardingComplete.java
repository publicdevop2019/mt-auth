package com.mt.access.domain.model.user_relation.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProjectOnboardingComplete extends DomainEvent {
    public static final String PROJECT_ONBOARDING_COMPLETED = "project_onboarding_completed";
    public static final String name = "PROJECT_ONBOARDING_COMPLETED";
    @Getter
    private UserId creator;
    @Getter
    private String projectName;

    public ProjectOnboardingComplete(Project project) {
        super(project.getProjectId());
        setTopic(PROJECT_ONBOARDING_COMPLETED);
        setName(name);
        projectName = project.getName();
        this.creator = new UserId(project.getCreatedBy());
    }
}
