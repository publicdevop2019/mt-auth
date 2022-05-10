package com.mt.access.application.project.representation;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.user.User;
import java.util.Optional;
import lombok.Data;

@Data
public class ProjectRepresentation {
    private final String id;
    private final String createdBy;
    private final Long createdAt;
    private String creatorName;
    private String name;

    public ProjectRepresentation(Project project) {
        this.name = project.getName();
        this.id = project.getProjectId().getDomainId();
        this.createdBy = project.getCreatedBy();
        Optional<User> user =
            ApplicationServiceRegistry.getUserApplicationService().user(this.createdBy);
        user.ifPresent(e -> this.creatorName = e.getDisplayName());
        this.createdAt = project.getCreatedAt().getTime();
    }
}
