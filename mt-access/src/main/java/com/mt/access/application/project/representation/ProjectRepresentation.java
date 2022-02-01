package com.mt.access.application.project.representation;

import com.mt.access.domain.model.project.Project;
import lombok.Data;

@Data
public class ProjectRepresentation {
    private String name;
    public ProjectRepresentation(Project project) {
        this.name= project.getName();
    }
}
