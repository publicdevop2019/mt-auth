package com.mt.access.application.role.representation;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleType;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;

@Data
public class RoleRepresentation {
    private final RoleType roleType;
    private String id;
    private String name;
    private String description;
    private String originalName;
    private String parentId;
    private Set<String> apiPermissionIds;
    private Set<PermissionDetail> permissionDetails;
    private Set<String> commonPermissionIds;
    private Set<String> externalPermissionIds;
    private boolean systemCreate;

    public RoleRepresentation(Role role) {
        this.id = role.getRoleId().getDomainId();
        this.name = role.getName();
        this.description = role.getDescription();
        if (role.getParentId() != null) {
            this.parentId = role.getParentId().getDomainId();
        }
        this.originalName = role.getName();
        this.systemCreate = role.isSystemCreate();
        this.roleType = role.getType();
        if (role.getApiPermissionIds() != null) {
            this.apiPermissionIds = role.getApiPermissionIds().stream().map(DomainId::getDomainId)
                .collect(Collectors.toSet());
        }
        if (role.getCommonPermissionIds() != null) {
            this.commonPermissionIds =
                role.getCommonPermissionIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet());
        }
        if (role.getExternalPermissionIds() != null) {
            this.externalPermissionIds =
                role.getExternalPermissionIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet());
        }
        if (this.roleType.equals(RoleType.CLIENT)) {
            Client client =
                DomainRegistry.getClientRepository().get(new ClientId(role.getName()));
            this.name = client.getName();

        }
        if (this.roleType.equals(RoleType.PROJECT)) {
            Project byId =
                DomainRegistry.getProjectRepository().get(new ProjectId(role.getName()));
                this.name = byId.getName();
        }
        permissionDetails = new HashSet<>();
        if (role.getCommonPermissionIds() != null && !role.getCommonPermissionIds().isEmpty()) {
            Set<Permission> allByQuery =
                QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository()
                    .query(e), new PermissionQuery(role.getCommonPermissionIds()));

            Set<PermissionDetail> collect =
                allByQuery.stream().map(e -> new PermissionDetail(e.getPermissionId(), e.getName()))
                    .collect(
                        Collectors.toSet());
            permissionDetails.addAll(collect);
        }
        if (role.getApiPermissionIds() != null && !role.getApiPermissionIds().isEmpty()) {
            Set<Permission> allByQuery =
                QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository()
                    .query(e), new PermissionQuery(role.getApiPermissionIds()));
            Set<EndpointId> collect1 = allByQuery.stream().map(e -> new EndpointId(e.getName()))
                .collect(Collectors.toSet());
            Set<Endpoint> allByQuery1 = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getEndpointRepository().query(e),
                    new EndpointQuery(collect1));
            Set<PermissionDetail> collect =
                allByQuery.stream()
                    .map(e -> new PermissionDetail(e.getPermissionId(), e.getName(), allByQuery1))
                    .collect(
                        Collectors.toSet());
            permissionDetails.addAll(collect);
        }
    }

    @Getter
    private static class PermissionDetail {
        private final String id;
        private String name;

        public PermissionDetail(PermissionId permissionId, String name) {
            id = permissionId.getDomainId();
            this.name = name;
        }

        public PermissionDetail(PermissionId permissionId, String name, Set<Endpoint> allByQuery1) {
            id = permissionId.getDomainId();
            this.name = name;
            allByQuery1.stream().filter(e -> e.getEndpointId().getDomainId().equals(name))
                .findFirst().ifPresent(e -> this.name = e.getName());
        }
    }
}
