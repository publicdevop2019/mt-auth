package com.mt.access.application.project.representation;

import com.mt.access.domain.model.project.Project;
import lombok.Data;

@Data
public class ProjectCardRepresentation {
    private final String name;
    private final String id;

    public ProjectCardRepresentation(Project project) {
        this.name = project.getName();
        this.id = project.getProjectId().getDomainId();

    }
}
