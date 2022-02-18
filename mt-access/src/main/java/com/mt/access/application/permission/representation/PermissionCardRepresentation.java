package com.mt.access.application.permission.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class PermissionCardRepresentation {
    private String name;
    private String originalName;
    private String tenantId;
    private String tenantName;
    private String id;
    private String parentId;
    private boolean systemCreate;
    private PermissionType type;

    public PermissionCardRepresentation(Permission permission) {
        this.name = permission.getName();
        this.systemCreate = permission.isSystemCreate();
        this.type = permission.getType();
        if (permission.getParentId() != null) {
            this.parentId = permission.getParentId().getDomainId();
        }
        this.id = permission.getPermissionId().getDomainId();
        if (permission.getTenantId() != null)
            this.tenantId = permission.getTenantId().getDomainId();
    }

    public static SumPagedRep<PermissionCardRepresentation> updateName(SumPagedRep<PermissionCardRepresentation> response) {
        List<PermissionCardRepresentation> data = response.getData();
        Set<ProjectId> collect = data.stream().filter(e -> e.type.equals(PermissionType.PROJECT)).map(e -> new ProjectId(e.name)).collect(Collectors.toSet());
        if (collect.size() > 0) {
            Set<Project> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery((ProjectQuery) e), new ProjectQuery(collect));
            data.forEach(e -> {
                allByQuery.stream().filter(ee -> ee.getProjectId().getDomainId().equals(e.name)).findFirst().ifPresent(ee -> {
                    e.name = ee.getName();
                });
            });
        }
        Set<EndpointId> collect1 = data.stream().filter(e -> e.type.equals(PermissionType.API)).map(e -> new EndpointId(e.name)).collect(Collectors.toSet());
        if (collect1.size() > 0) {
            Set<Endpoint> allByQuery2 = QueryUtility.getAllByQuery(e -> DomainRegistry.getEndpointRepository().endpointsOfQuery((EndpointQuery) e), new EndpointQuery(collect1));
            data.forEach(e -> {
                allByQuery2.stream().filter(ee -> ee.getEndpointId().getDomainId().equals(e.name)).findFirst().ifPresent(ee -> {
                    e.name = ee.getName();
                });
            });
        }
        Set<ProjectId> collect2 = data.stream().filter(e -> e.getTenantId() != null).map(e -> new ProjectId(e.tenantId)).collect(Collectors.toSet());
        if (collect2.size() > 0) {
            Set<Project> allByQuery2 = QueryUtility.getAllByQuery(e -> DomainRegistry.getProjectRepository().getByQuery((ProjectQuery) e), new ProjectQuery(collect2));
            data.forEach(e -> {
                allByQuery2.stream().filter(ee -> ee.getProjectId().getDomainId().equals(e.tenantId)).findFirst().ifPresent(ee -> {
                    e.tenantName = ee.getName();
                });
            });
        }
        return response;
    }
}
