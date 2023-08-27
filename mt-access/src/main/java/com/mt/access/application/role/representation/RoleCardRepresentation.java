package com.mt.access.application.role.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleType;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Data;

@Data
public class RoleCardRepresentation {
    private final String description;
    private final RoleType roleType;
    private final String id;
    private String name;
    private String tenantId;
    private Boolean systemCreate;

    public RoleCardRepresentation(Role role) {
        this.id = role.getRoleId().getDomainId();
        this.name = role.getName();
        this.systemCreate = role.getSystemCreate();
        this.description = role.getDescription();
        this.roleType = role.getType();
        if (role.getTenantId() != null) {
            this.tenantId = role.getTenantId().getDomainId();
        }
    }
}
