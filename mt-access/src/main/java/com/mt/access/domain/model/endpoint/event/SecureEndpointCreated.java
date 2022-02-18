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
    private PermissionId permissionId;

    public SecureEndpointCreated(ProjectId projectId, Endpoint endpoint) {
        super();
        setTopic(SECURE_ENDPOINT_CREATED);
        setName(name);
        setDomainId(endpoint.getEndpointId());
        this.projectId = projectId;
        this.permissionId = endpoint.getPermissionId();
    }
}
