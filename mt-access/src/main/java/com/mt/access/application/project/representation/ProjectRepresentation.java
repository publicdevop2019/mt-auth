package com.mt.access.application.project.representation;

import com.mt.access.domain.model.project.Project;
import lombok.Data;

@Data
public class ProjectRepresentation {
    private String name;
    private final String id;
    private final String createdBy;
    private final Long createdAt;
    public ProjectRepresentation(Project project) {
        this.name= project.getName();
        this.id = project.getProjectId().getDomainId();
        this.createdBy = project.getCreatedBy();
        this.createdAt = project.getCreatedAt().getTime();
    }
}
