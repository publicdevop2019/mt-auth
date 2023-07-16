package com.mt.access.domain.model.permission.event;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ProjectPermissionCreated extends DomainEvent {

    public static final String PROJECT_PERMISSION_CREATED = "project_permission_created";
    public static final String name = "PROJECT_PERMISSION_CREATED";
    @Getter
    private UserId creator;
    @Getter
    private ProjectId projectId;
    @Getter
    private Set<PermissionId> commonPermissionIds;
    @Getter
    private Set<PermissionId> linkedPermissionIds;

    {
        setTopic(PROJECT_PERMISSION_CREATED);
        setName(name);

    }

    public ProjectPermissionCreated(Set<PermissionId> commonPermissionIds,
                                    Set<PermissionId> linkedPermissionIds, ProjectId projectId,
                                    UserId userId) {
        super(projectId);
        this.creator = userId;
        this.projectId = projectId;
        this.commonPermissionIds = commonPermissionIds;
        this.linkedPermissionIds = linkedPermissionIds;
    }
}
