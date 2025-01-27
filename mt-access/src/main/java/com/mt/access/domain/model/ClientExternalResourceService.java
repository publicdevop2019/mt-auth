package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientExternalResource;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ClientExternalResourceService {
    public void handle(ProjectId projectId) {
        Set<Client> allClients = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                new ClientQuery(projectId));
        Set<Role> allRoles = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                new RoleQuery(projectId));
        Set<PermissionId> externalPermissions =
            allRoles.stream().filter(e -> e.getExternalPermissionIds() != null)
                .flatMap(e -> e.getExternalPermissionIds().stream())
                .collect(Collectors.toSet());
        if (!externalPermissions.isEmpty()) {
            Set<Endpoint> allEps = QueryUtility.getAllByQuery(e ->
                    DomainRegistry.getEndpointRepository().query(e),
                EndpointQuery.permissionQuery(externalPermissions));
            Set<ClientId> allExternalClientIds =
                allEps.stream().map(Endpoint::getClientId)
                    .collect(Collectors.toSet());
            //all clients has same external resources
            allClients.forEach(client -> {
                Set<ClientId> externalResources =
                    DomainRegistry.getClientExternalResourceRepository().query(client);
                ClientExternalResource.update(client, externalResources,
                    allExternalClientIds);
            });
        }
    }
}
