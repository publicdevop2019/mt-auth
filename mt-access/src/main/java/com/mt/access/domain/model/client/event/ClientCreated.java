package com.mt.access.domain.model.client.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.domain.model.domain_event.DomainEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AuditEvent
public class ClientCreated extends DomainEvent {

    public static final String CLIENT_CREATED = "client_created";
    public static final String name = "CLIENT_CREATED";
    @Getter
    private RoleId roleId;
    @Getter
    private ProjectId projectId;
    @Getter
    private String changeId;

    {
        setTopic(CLIENT_CREATED);
        setName(name);

    }

    public ClientCreated(Client client) {
        super(client.getClientId());
        this.roleId = client.getRoleId();
        this.projectId = client.getProjectId();
        this.changeId = client.getClientId().getDomainId();
    }
}
