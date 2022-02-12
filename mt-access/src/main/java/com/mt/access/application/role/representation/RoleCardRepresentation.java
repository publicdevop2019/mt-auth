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
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class RoleCardRepresentation {
    private final String description;
    private final RoleType roleType;
    private final String id;
    private String name;
    private String tenantId;

    public RoleCardRepresentation(Role role) {
        this.id = role.getRoleId().getDomainId();
        this.name = role.getName();
        this.description = role.getDescription();
        this.roleType = role.getType();
        if (role.getTenantId() != null) {
            this.tenantId = role.getTenantId().getDomainId();
        }
    }

    public static SumPagedRep<RoleCardRepresentation> updateName(SumPagedRep<RoleCardRepresentation> response) {
        List<RoleCardRepresentation> data = response.getData();
        Set<ProjectId> collect = data.stream().filter(e -> e.roleType.equals(RoleType.PROJECT)).flatMap(e -> Stream.of(new ProjectId(e.name), new ProjectId(e.tenantId))).collect(Collectors.toSet());
        Set<ProjectId> collect2 = data.stream().filter(e -> e.tenantId != null).map(e -> new ProjectId(e.tenantId)).collect(Collectors.toSet());
        collect.addAll(collect2);
        if (collect.size() > 0) {
            Set<Project> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery((ProjectQuery) e), new ProjectQuery(collect));
            data.forEach(e -> {
                if (e.roleType.equals(RoleType.PROJECT)) {
                    allByQuery.stream().filter(ee -> ee.getProjectId().getDomainId().equals(e.name)).findFirst().ifPresent(ee -> {
                        e.name = ee.getName();
                    });
                }
                if (e.tenantId != null) {
                    allByQuery.stream().filter(ee2 -> ee2.getProjectId().getDomainId().equals(e.tenantId)).findFirst().ifPresent(eee -> {
                        e.tenantId = eee.getName();
                    });
                }
            });
        }
        Set<ClientId> collect1 = data.stream().filter(e -> e.roleType.equals(RoleType.CLIENT)).map(e -> new ClientId(e.name)).collect(Collectors.toSet());
        if (collect1.size() > 0) {
            Set<Client> allByQuery2 = QueryUtility.getAllByQuery(e -> DomainRegistry.getClientRepository().clientsOfQuery((ClientQuery) e), new ClientQuery(collect1));
            data.forEach(e -> {
                if (e.roleType.equals(RoleType.CLIENT)) {
                    allByQuery2.stream().filter(ee -> ee.getClientId().getDomainId().equals(e.name)).findFirst().ifPresent(ee -> {
                        e.name = ee.getName();
                    });
                }
            });
        }
        return response;
    }
}
