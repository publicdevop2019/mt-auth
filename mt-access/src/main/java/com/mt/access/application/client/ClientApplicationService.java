package com.mt.access.application.client;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_CLIENT;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_CLIENT;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_CLIENT;
import static com.mt.access.domain.model.permission.Permission.CLIENT_MGMT;

import com.mt.access.application.client.command.ClientCreateCommand;
import com.mt.access.application.client.command.ClientUpdateCommand;
import com.mt.access.application.client.representation.ClientAutoApproveRepresentation;
import com.mt.access.application.client.representation.ClientCardRepresentation;
import com.mt.access.application.client.representation.ClientDropdownRepresentation;
import com.mt.access.application.client.representation.ClientMgmtCardRepresentation;
import com.mt.access.application.client.representation.ClientRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.ClientResource;
import com.mt.access.domain.model.client.ExternalUrl;
import com.mt.access.domain.model.client.GrantType;
import com.mt.access.domain.model.client.RedirectUrl;
import com.mt.access.domain.model.client.TokenDetail;
import com.mt.access.domain.model.client.event.ClientAsResourceDeleted;
import com.mt.access.domain.model.client.event.ClientResourceCleanUpCompleted;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.event.ExternalPermissionUpdated;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Utility;
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
        return new SumPagedRep<>(clients, ClientCardRepresentation::new);
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
        Set<ClientId> resources = DomainRegistry.getClientResourceRepository().query(client);
        Set<RedirectUrl> urls = DomainRegistry.getClientRedirectUrlRepository().query(client);
        Set<GrantType> grantTypes = DomainRegistry.getClientGrantTypeRepository().query(client);
        return new ClientRepresentation(client, resources, urls, grantTypes);
    }

    public SumPagedRep<ClientMgmtCardRepresentation> mgmtQuery(String queryParam,
                                                               String pagingParam,
                                                               String configParam) {
        ClientQuery clientQuery = new ClientQuery(queryParam, pagingParam, configParam);
        SumPagedRep<Client> clients =
            DomainRegistry.getClientRepository().query(clientQuery);
        SumPagedRep<ClientMgmtCardRepresentation> rep =
            new SumPagedRep<>(clients, (c) -> {
                Set<ClientId> query = DomainRegistry.getClientResourceRepository().query(c);
                Set<RedirectUrl> urls = DomainRegistry.getClientRedirectUrlRepository().query(c);
                Set<GrantType> grantTypes = DomainRegistry.getClientGrantTypeRepository().query(c);
                return new ClientMgmtCardRepresentation(c, query, urls, grantTypes);
            });
        List<ClientMgmtCardRepresentation> data = rep.getData();
        Set<ClientId> collect = data.stream().filter(e -> e.getResourceIds() != null)
            .flatMap(e -> e.getResourceIds().stream()).map(ClientId::new)
            .collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            Set<Client> allByIds = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                    new ClientQuery(collect));
            data.forEach(e -> {
                if (e.getResourceIds() != null) {
                    Set<ClientMgmtCardRepresentation.ResourceClientInfo> collect1 =
                        e.getResourceIds().stream().map(ee -> {
                            Optional<Client> first = allByIds.stream()
                                .filter(el -> el.getClientId().getDomainId().equals(ee))
                                .findFirst();
                            return first.map(
                                    client -> new ClientMgmtCardRepresentation.ResourceClientInfo(
                                        client.getName(), ee))
                                .orElseGet(
                                    () -> new ClientMgmtCardRepresentation.ResourceClientInfo(ee,
                                        ee));
                        }).collect(Collectors.toSet());
                    e.setResources(collect1);
                }
            });
        }
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
        Set<ClientId> resources = DomainRegistry.getClientResourceRepository().query(client);
        Set<RedirectUrl> urls = DomainRegistry.getClientRedirectUrlRepository().query(client);
        Set<GrantType> grantTypes = DomainRegistry.getClientGrantTypeRepository().query(client);
        return new ClientRepresentation(client, resources, urls, grantTypes);
    }

    public SumPagedRep<Client> proxyQuery(String pagingParam, String configParam) {
        return DomainRegistry.getClientRepository()
            .query(ClientQuery.internalQuery(pagingParam, configParam));
    }

    public ClientAutoApproveRepresentation getAuthorizeInfo(String rawProjectId, String id) {
        ProjectId projectId = new ProjectId(rawProjectId);
        Project project = DomainRegistry.getProjectRepository().query(projectId);
        Client client = DomainRegistry.getClientRepository().get(projectId, new ClientId(id));
        return new ClientAutoApproveRepresentation(project, client);
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
                        new TokenDetail(command.getAccessTokenValiditySeconds(),
                            command.getRefreshTokenValiditySeconds()),
                        command.getTypes(),
                        command.getExternalUrl() != null ?
                            new ExternalUrl(command.getExternalUrl()) : null,
                        context
                    );
                    Set<RedirectUrl> redirectUrls =
                        Utility.mapToSet(command.getRegisteredRedirectUri(), RedirectUrl::new);
                    GrantType.add(client, command.getGrantTypeEnums(), redirectUrls);
                    Set<ClientId> resources =
                        Utility.mapToSet(command.getResourceIds(), ClientId::new);
                    ClientResource.add(client, resources);
                    RedirectUrl.add(client, command.getGrantTypeEnums(), redirectUrls);
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
                ProjectId projectId = new ProjectId(command.getProjectId());
                ClientQuery clientQuery =
                    new ClientQuery(clientId, projectId);
                Optional<Client> optionalClient =
                    DomainRegistry.getClientRepository().query(clientQuery).findFirst();
                if (optionalClient.isPresent()) {
                    Client client = optionalClient.get();
                    Client updated = client.update(
                        command.getName(),
                        command.getClientSecret(),
                        command.getPath(),
                        command.getDescription(),
                        command.getResourceIndicator(),
                        new TokenDetail(command.getAccessTokenValiditySeconds(),
                            command.getRefreshTokenValiditySeconds()),
                        command.getExternalUrl() != null ?
                            new ExternalUrl(command.getExternalUrl()) : null,
                        context
                    );
                    Set<ClientId> newResources =
                        Utility.mapToSet(command.getResourceIds(), ClientId::new);
                    Set<ClientId> oldResources =
                        DomainRegistry.getClientResourceRepository().query(client);
                    ClientResource.update(updated, oldResources, newResources, context);
                    Set<RedirectUrl> existingUrl =
                        DomainRegistry.getClientRedirectUrlRepository().query(client);
                    Set<RedirectUrl> newUrls =
                        Utility.mapToSet(command.getRegisteredRedirectUri(), RedirectUrl::new);
                    RedirectUrl.update(updated, command.getGrantTypeEnums(), existingUrl, newUrls
                    );
                    Set<GrantType> grantTypes =
                        DomainRegistry.getClientGrantTypeRepository().query(client);
                    GrantType.update(client, grantTypes, command.getGrantTypeEnums(), newUrls,
                        context);
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
                Optional<Client> optionalClient =
                    DomainRegistry.getClientRepository().query(clientQuery).findFirst();
                if (optionalClient.isPresent()) {
                    Client client = optionalClient.get();
                    DomainRegistry.getAuditService().storeAuditAction(DELETE_TENANT_CLIENT,
                        client);
                    DomainRegistry.getAuditService().logUserAction(log, DELETE_TENANT_CLIENT,
                        client);
                    Set<ClientId> resources =
                        DomainRegistry.getClientResourceRepository().query(client);
                    DomainRegistry.getClientResourceRepository().removeAll(client, resources);
                    Set<ClientId> extSrc =
                        DomainRegistry.getClientExternalResourceRepository().query(client);
                    DomainRegistry.getClientExternalResourceRepository().removeAll(client, extSrc);
                    Set<RedirectUrl> urls =
                        DomainRegistry.getClientRedirectUrlRepository().query(client);
                    DomainRegistry.getClientRedirectUrlRepository().removeAll(client, urls);
                    Set<GrantType> grantTypes =
                        DomainRegistry.getClientGrantTypeRepository().query(client);
                    DomainRegistry.getClientGrantTypeRepository().removeAll(client, grantTypes);
                    client.remove(context);
                }
                return null;
            }, CLIENT);
    }

    public void handle(ClientAsResourceDeleted event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                //remove deleted client from resource_map
                ClientId removedClientId = new ClientId(event.getDomainId().getDomainId());
                //for all ref client for revoke
                Set<Client> refClients = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getClientRepository().query(query),
                    ClientQuery.resourceIds(removedClientId));
                Set<ClientId> refClientIds =
                    refClients.stream().map(Client::getClientId).collect(Collectors.toSet());
                refClientIds.add(removedClientId);
                context.append(new ClientResourceCleanUpCompleted(refClientIds));
                //delete all ref
                DomainRegistry.getClientResourceRepository().removeRef(removedClientId);
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
                DomainRegistry.getClientExternalResourceService().handle(projectId);
                return null;
            }, CLIENT);
    }

}
