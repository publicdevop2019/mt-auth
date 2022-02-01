package com.mt.access.application.project.command;

import com.mt.access.domain.model.project.Project;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ProjectPatchCommand {
    private String name;

    public ProjectPatchCommand(Project project) {
        this.name = project.getName();
    }
}
