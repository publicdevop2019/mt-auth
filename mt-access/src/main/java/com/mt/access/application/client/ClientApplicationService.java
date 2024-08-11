package com.mt.access.application.client;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_CLIENT;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_CLIENT;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_CLIENT;
import static com.mt.access.domain.model.permission.Permission.CLIENT_MGMT;

import com.mt.access.application.client.command.ClientCreateCommand;
import com.mt.access.application.client.command.ClientUpdateCommand;
import com.mt.access.application.client.representation.ClientCardRepresentation;
import com.mt.access.application.client.representation.ClientDropdownRepresentation;
import com.mt.access.application.client.representation.ClientOAuth2Representation;
import com.mt.access.application.client.representation.ClientRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.ExternalUrl;
import com.mt.access.domain.model.client.TokenDetail;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.develop.Analytics;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ClientApplicationService {

    private static final String CLIENT = "Client";

    public SumPagedRep<ClientCardRepresentation> tenantQuery(String queryParam, String pagingParam,
                                                             String configParam) {
        ClientQuery clientQuery = new ClientQuery(queryParam, pagingParam, configParam);
        DomainRegistry.getPermissionCheckService()
            .canAccess(clientQuery.getProjectIds(), CLIENT_MGMT);
        SumPagedRep<Client> clients =
            DomainRegistry.getClientRepository().query(clientQuery);
        SumPagedRep<ClientCardRepresentation> rep =
            new SumPagedRep<>(clients, ClientCardRepresentation::new);
        updateDetails(rep.getData());
        return rep;
    }

    private static void updateDetails(List<ClientCardRepresentation> data) {
        Set<ClientId> collect = data.stream().filter(e -> e.getResourceIds() != null)
            .flatMap(e -> e.getResourceIds().stream()).map(ClientId::new)
            .collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            Set<Client> allByIds = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                    new ClientQuery(collect));
            data.forEach(e -> {
                if (e.getResourceIds() != null) {
                    Set<ClientCardRepresentation.ResourceClientInfo> collect1 =
                        e.getResourceIds().stream().map(ee -> {
                            Optional<Client> first = allByIds.stream()
                                .filter(el -> el.getClientId().getDomainId().equals(ee))
                                .findFirst();
                            return first.map(
                                    client -> new ClientCardRepresentation.ResourceClientInfo(
                                        client.getName(), ee))
                                .orElseGet(
                                    () -> new ClientCardRepresentation.ResourceClientInfo(ee, ee));
                        }).collect(Collectors.toSet());
                    e.setResources(collect1);
                }
            });
        }
    }

    /**
     * query client for dropdown with the best performance for client
     *
     * @param queryParam  query string
     * @param pagingParam page string
     * @param configParam config string
     * @return paginated dropdown client
     */
    public SumPagedRep<ClientDropdownRepresentation> tenantDropdownQuery(String queryParam,
                                                                         String pagingParam,
                                                                         String configParam) {
        ClientQuery clientQuery =
            ClientQuery.dropdownQuery(queryParam, pagingParam, configParam, 1000);
        DomainRegistry.getPermissionCheckService()
            .canAccess(clientQuery.getProjectIds(), CLIENT_MGMT);
        SumPagedRep<Client> clients = DomainRegistry.getClientRepository().query(clientQuery);
        return new SumPagedRep<>(clients, ClientDropdownRepresentation::new);
    }

    public ClientRepresentation tenantQueryById(String clientId, String projectId) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId1, CLIENT_MGMT);
        Client client =
            DomainRegistry.getClientRepository().get(projectId1, new ClientId(clientId));
        return new ClientRepresentation(client);
    }

    public SumPagedRep<ClientCardRepresentation> mgmtQuery(String queryParam, String pagingParam,
                                                           String configParam) {
        ClientQuery clientQuery = new ClientQuery(queryParam, pagingParam, configParam);
        SumPagedRep<Client> clients =
            DomainRegistry.getClientRepository().query(clientQuery);
        SumPagedRep<ClientCardRepresentation> rep =
            new SumPagedRep<>(clients, ClientCardRepresentation::new);
        updateDetails(rep.getData());
        return rep;
    }

    /**
     * query client for dropdown with best performance
     *
     * @param queryParam  query string
     * @param pagingParam page string
     * @param configParam config string
     * @return paginated dropdown client
     */
    public SumPagedRep<ClientDropdownRepresentation> mgmtDropdownQuery(String queryParam,
                                                                       String pagingParam,
                                                                       String configParam) {
        ClientQuery clientQuery =
            ClientQuery.dropdownQuery(queryParam, pagingParam, configParam, 1000);
        SumPagedRep<Client> clients = DomainRegistry.getClientRepository().query(clientQuery);
        return new SumPagedRep<>(clients, ClientDropdownRepresentation::new);
    }

    public ClientRepresentation mgmtQueryById(String id) {
        Client client = DomainRegistry.getClientRepository().get(new ClientId(id));
        return new ClientRepresentation(client);
    }

    public SumPagedRep<Client> proxyQuery(String pagingParam, String configParam) {
        return DomainRegistry.getClientRepository()
            .query(ClientQuery.internalQuery(pagingParam, configParam));
    }

    public Client canAutoApprove(String projectId, String id) {
        return
            DomainRegistry.getClientRepository().get(new ProjectId(projectId), new ClientId(id));
    }

    @AuditLog(actionName = CREATE_TENANT_CLIENT)
    public String tenantCreate(ClientCreateCommand command, String changeId) {
        ClientId clientId = new ClientId();
        DomainRegistry.getPermissionCheckService()
            .canAccess(new ProjectId(command.getProjectId()), CLIENT_MGMT);
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId,
                (context) -> {
                    Client client = new Client(
                        clientId,
                        new ProjectId(command.getProjectId()),
                        command.getName(),
                        command.getPath(),
                        command.getClientSecret(),
                        command.getDescription(),
                        command.getResourceIndicator(),
                        command.getResourceIds() != null
                            ? command.getResourceIds().stream().map(ClientId::new)
                            .collect(Collectors.toSet()) : Collections.emptySet(),
                        command.getGrantTypeEnums(),
                        new TokenDetail(command.getAccessTokenValiditySeconds(),
                            command.getRefreshTokenValiditySeconds()),
                        command.getRegisteredRedirectUri(),
                        command.getAutoApprove(),
                        command.getTypes(),
                        command.getExternalUrl() != null ?
                            new ExternalUrl(command.getExternalUrl()) : null,
                        context
                    );
                    DomainRegistry.getClientRepository().add(client);
                    return client.getClientId().getDomainId();
                }, CLIENT
            );

    }

    @AuditLog(actionName = UPDATE_TENANT_CLIENT)
    public void tenantUpdate(String id, ClientUpdateCommand command, String changeId) {
        ClientId clientId = new ClientId(id);
        DomainRegistry.getPermissionCheckService()
            .canAccess(new ProjectId(command.getProjectId()), CLIENT_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                ClientQuery clientQuery =
                    new ClientQuery(clientId, new ProjectId(command.getProjectId()));
                Optional<Client> optionalClient =
                    DomainRegistry.getClientRepository().query(clientQuery).findFirst();
                if (optionalClient.isPresent()) {
                    Client client = optionalClient.get();
                    Client replace = client.replace(
                        command.getName(),
                        command.getClientSecret(),
                        command.getPath(),
                        command.getDescription(),
                        command.getResourceIndicator(),
                        command.getResourceIds() != null
                            ?
                            command.getResourceIds().stream().map(ClientId::new)
                                .collect(Collectors.toSet())
                            : Collections.emptySet(),
                        command.getGrantTypeEnums(),
                        new TokenDetail(command.getAccessTokenValiditySeconds(),
                            command.getRefreshTokenValiditySeconds()),
                        command.getRegisteredRedirectUri(),
                        command.getAutoApprove(),
                        command.getExternalUrl() != null ?
                            new ExternalUrl(command.getExternalUrl()) : null,
                        context
                    );
                    DomainRegistry.getClientRepository().update(client, replace);
                }
                return null;
            }, CLIENT);
    }

    @AuditLog(actionName = DELETE_TENANT_CLIENT)
    public void tenantRemove(String projectId, String id, String changeId) {
        ClientId clientId = new ClientId(id);
        DomainRegistry.getPermissionCheckService().canAccess(new ProjectId(projectId), CLIENT_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                ClientQuery clientQuery = new ClientQuery(clientId, new ProjectId(projectId));
                Optional<Client> client =
                    DomainRegistry.getClientRepository().query(clientQuery).findFirst();
                if (client.isPresent()) {
                    Client client1 = client.get();
                    if (client1.removable()) {
                        DomainRegistry.getClientRepository().remove(client1);
                        client1.removeAllReferenced(context);
                        DomainRegistry.getAuditService()
                            .storeAuditAction(DELETE_TENANT_CLIENT,
                                client1);
                        DomainRegistry.getAuditService()
                            .logUserAction(log, DELETE_TENANT_CLIENT,
                                client1);
                    } else {
                        throw new DefinedRuntimeException("client cannot be deleted", "1009",
                            HttpResponseCode.BAD_REQUEST);
                    }
                }
                return null;
            }, CLIENT);
    }

    public ClientOAuth2Representation getClientBy(String id) {
        Analytics start = Analytics.start(Analytics.Type.LOAD_CLIENT_FOR_LOGIN);
        log.debug("loading client by id started");
        Client client =
            DomainRegistry.getClientRepository().query(new ClientId(id));
        log.debug("loading client by id end");
        start.stop();
        if (client == null) {
            return null;
        }
        return new ClientOAuth2Representation(client);
    }

    public void handle(ClientAsResourceDeleted event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                //remove deleted client from resource_map
                DomainId domainId = event.getDomainId();
                ClientId removedClientId = new ClientId(domainId.getDomainId());
                //for all ref client for revoke
                Set<Client> refClients = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getClientRepository().query(query),
                    ClientQuery.resourceIds(removedClientId));
                Set<ClientId> refClientIds =
                    refClients.stream().map(Client::getClientId).collect(Collectors.toSet());
                refClientIds.add(removedClientId);
                context.append(new ClientResourceCleanUpCompleted(refClientIds));
                //delete all ref
                DomainRegistry.getClientRepository().removeRef(removedClientId);
                return null;
            }, CLIENT);
    }

    /**
     * update project all client's external resource list after project external permission updated.
     *
     * @param event ExternalPermissionUpdated
     */
    public void handle(ExternalPermissionUpdated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                ProjectId projectId = new ProjectId(event.getDomainId().getDomainId());
                Set<Client> projectClients = QueryUtility
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
                    Set<Endpoint> referredClients = QueryUtility.getAllByQuery(e ->
                            DomainRegistry.getEndpointRepository().query(e),
                        EndpointQuery.permissionQuery(externalPermissions));
                    Set<ClientId> collect =
                        referredClients.stream().map(Endpoint::getClientId)
                            .collect(Collectors.toSet());
                    projectClients.forEach(client -> client.updateExternalResource(collect));
                }
                return null;
            }, CLIENT);
    }

}
