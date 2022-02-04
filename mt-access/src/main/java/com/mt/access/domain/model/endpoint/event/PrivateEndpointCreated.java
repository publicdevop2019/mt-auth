package com.mt.access.domain.model.endpoint.event;

import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PrivateEndpointCreated extends DomainEvent {
    public static final String PRIVATE_ENDPOINT_CREATED = "private_endpoint_created";
    public static final String name = "PRIVATE_ENDPOINT_CREATED";
    private ProjectId projectId;

    public PrivateEndpointCreated(ProjectId projectId, EndpointId endpointId) {
        super();
        setTopic(PRIVATE_ENDPOINT_CREATED);
        setName(name);
        setDomainId(endpointId);
        this.projectId = projectId;
    }
}
