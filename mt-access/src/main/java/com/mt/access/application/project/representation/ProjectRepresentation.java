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
    private Long totalClient;
    private Long totalEndpoint;
    private Long totalUserOwned;
    private Long totalPermissionCreated;
    private Long totalRoleCreated;

    public ProjectRepresentation(Project project, long clientCount, long epCount, long userCount,
                                 long permissionCount, long roleCount) {
        this.name = project.getName();
        this.id = project.getProjectId().getDomainId();
        this.createdBy = project.getCreatedBy();
        Optional<User> user =
            ApplicationServiceRegistry.getUserApplicationService().query(this.createdBy);
        user.ifPresent(e -> this.creatorName = e.getDisplayName());
        this.createdAt = project.getCreatedAt().getTime();
        this.totalClient = clientCount;
        this.totalEndpoint = epCount;
        this.totalUserOwned = userCount;
        this.totalPermissionCreated = permissionCount;
        this.totalRoleCreated = roleCount;
    }
}
