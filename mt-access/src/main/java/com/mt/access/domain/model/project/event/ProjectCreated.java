package com.mt.access.domain.model.project.event;

import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProjectCreated extends DomainEvent {
    public static final String PROJECT_CREATED = "project_created";
    public static final String name = "PROJECT_CREATED";
    @Getter
    private UserId creator;
    @Getter
    private String projectName;

    public ProjectCreated(Project project) {
        super(project.getProjectId());
        setTopic(PROJECT_CREATED);
        setName(name);
        projectName = project.getName();
        this.creator = new UserId(project.getCreatedBy());
    }
}
