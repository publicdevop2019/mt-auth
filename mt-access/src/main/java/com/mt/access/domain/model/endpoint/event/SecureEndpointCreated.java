package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SecureEndpointCreated extends DomainEvent {
    public static final String SECURE_ENDPOINT_CREATED = "secure_endpoint_created";
    public static final String name = "SECURE_ENDPOINT_CREATED";
    private ProjectId projectId;
    private Boolean shared;
    private PermissionId permissionId;
    private String changeId;

    {
        setTopic(SECURE_ENDPOINT_CREATED);
        setName(name);

    }

    public SecureEndpointCreated(ProjectId projectId, Endpoint endpoint) {
        super(endpoint.getEndpointId());
        this.projectId = projectId;
        this.shared = endpoint.getShared();
        this.permissionId = endpoint.getPermissionId();
        this.changeId = endpoint.getPermissionId().getDomainId();
    }
}
