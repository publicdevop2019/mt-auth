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
import com.mt.common.domain.model.validate.Utility;
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
    private Boolean systemCreate;
    private Integer version;

    public RoleRepresentation(Role role) {
        this.id = role.getRoleId().getDomainId();
        this.name = role.getName();
        this.version = role.getVersion();
        this.description = role.getDescription();
        if (role.getParentId() != null) {
            this.parentId = role.getParentId().getDomainId();
        }
        this.originalName = role.getName();
        this.systemCreate = role.getSystemCreate();
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
        if (Utility.notNullOrEmpty(role.getCommonPermissionIds())) {
            Set<Permission> permissions =
                QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository()
                    .query(e), PermissionQuery.internalQuery(role.getCommonPermissionIds()));

            Set<PermissionDetail> details =
                permissions.stream()
                    .map(e -> new PermissionDetail(e.getPermissionId(), e.getName()))
                    .collect(
                        Collectors.toSet());
            permissionDetails.addAll(details);
        }
        if (Utility.notNullOrEmpty(role.getApiPermissionIds())) {
            Set<Permission> permissions =
                new HashSet<>(
                    QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository()
                        .query(e), PermissionQuery.internalQuery(role.getApiPermissionIds())));
            Set<EndpointId> endpointIds =
                permissions.stream()
                    .map(e -> new EndpointId(e.getName()))
                    .collect(Collectors.toSet());
            Set<Endpoint> endpoints = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getEndpointRepository().query(e),
                    new EndpointQuery(endpointIds));
            Set<PermissionDetail> details =
                permissions.stream()
                    .map(e -> new PermissionDetail(e.getPermissionId(), e.getName(), endpoints,
                        PermissionType.API))
                    .collect(
                        Collectors.toSet());
            permissionDetails.addAll(details);
        }
        if (Utility.notNullOrEmpty(role.getExternalPermissionIds())) {
            Set<Permission> permissions =
                QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository()
                    .query(e), PermissionQuery.internalQuery(role.getExternalPermissionIds()));
            Set<EndpointId> endpointIds =
                permissions.stream()
                    .map(e -> new EndpointId(e.getName()))
                    .collect(Collectors.toSet());
            Set<Endpoint> endpoints = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getEndpointRepository().query(e),
                    new EndpointQuery(endpointIds));
            Set<PermissionDetail> details =
                permissions.stream()
                    .map(e -> new PermissionDetail(e.getPermissionId(), e.getName(),
                        endpoints, PermissionType.SHARED))
                    .collect(
                        Collectors.toSet());
            permissionDetails.addAll(details);
        }
    }

    private enum PermissionType {
        SHARED,
        API,
        COMMON;
    }

    @Getter
    private static class PermissionDetail {
        private final String id;
        private final PermissionType type;
        private String name;

        public PermissionDetail(PermissionId permissionId, String name) {
            id = permissionId.getDomainId();
            this.name = name;
            this.type = PermissionType.COMMON;
        }

        public PermissionDetail(PermissionId permissionId,
                                String name, Set<Endpoint> endpoints,
                                PermissionType type
        ) {
            id = permissionId.getDomainId();
            this.name = name;
            endpoints.stream().filter(e -> e.getEndpointId().getDomainId().equals(name))
                .findFirst().ifPresent(e -> this.name = e.getName());
            this.type = type;
        }
    }
}
