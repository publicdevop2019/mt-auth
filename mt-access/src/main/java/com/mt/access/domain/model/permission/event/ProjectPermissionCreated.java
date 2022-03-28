package com.mt.access.domain.model.permission.event;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.user.UserId;
import com.mt.common.domain.model.domain_id.DomainId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import java.util.Set;
import java.util.stream.Collectors;
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

    public ProjectPermissionCreated(Set<PermissionId> permissionIds, ProjectId projectId,
                                    UserId userId) {
        super(permissionIds.stream().map(e -> new DomainId(e.getDomainId()))
            .collect(Collectors.toSet()));
        setInternal(true);
        setTopic(PROJECT_PERMISSION_CREATED);
        setName(name);
        this.creator = userId;
        this.projectId = projectId;
    }
}
