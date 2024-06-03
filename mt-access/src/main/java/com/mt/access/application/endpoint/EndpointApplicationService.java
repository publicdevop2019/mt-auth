package com.mt.access.application.endpoint;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_ENDPOINT;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_ENDPOINT;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_ENDPOINT;
import static com.mt.access.domain.model.permission.Permission.API_MGMT;

import com.mt.access.application.endpoint.command.EndpointCreateCommand;
import com.mt.access.application.endpoint.command.EndpointExpireCommand;
import com.mt.access.application.endpoint.command.EndpointUpdateCommand;
import com.mt.access.application.endpoint.representation.EndpointCardRepresentation;
import com.mt.access.application.endpoint.representation.EndpointMgmtRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProtectedRepresentation;
import com.mt.access.application.endpoint.representation.EndpointProxyCacheRepresentation;
import com.mt.access.application.endpoint.representation.EndpointSharedCardRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cache_profile.event.CacheProfileRemoved;
import com.mt.access.domain.model.cache_profile.event.CacheProfileUpdated;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.cors_profile.event.CorsProfileRemoved;
import com.mt.access.domain.model.cors_profile.event.CorsProfileUpdated;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.event.EndpointCollectionModified;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.StoredEvent;
import com.mt.common.domain.model.domain_event.event.ApplicationStartedEvent;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EndpointApplicationService {
    private static final String ENDPOINT = "Endpoint";
    @Value("${mt.feature.proxy-reload}")
    private Boolean reloadOnAppStart;

    /**
     * send app started event with a delay of 60s to wait for registry complete.
     */
    @EventListener(ApplicationReadyEvent.class)
    protected void reloadProxy() {
        CommonDomainRegistry.getLogService().initTrace();
        if (reloadOnAppStart) {
            try {
                Thread.sleep(90 * 1000);
            } catch (InterruptedException e) {
                log.error("wait is interrupted due to", e);
            }
            log.debug("sending reload proxy endpoint message");
            CommonDomainRegistry.getEventStreamService()
                .next(StoredEvent.skipStoredEvent(new ApplicationStartedEvent()));
        }
    }

    public SumPagedRep<EndpointProxyCacheRepresentation> proxyQuery(String pageParam) {
        SumPagedRep<Endpoint> endpoints = DomainRegistry.getEndpointRepository()
            .query(new EndpointQuery(pageParam));
        List<EndpointProxyCacheRepresentation> collect =
            endpoints.getData().stream().map(EndpointProxyCacheRepresentation::new)
                .collect(Collectors.toList());
        EndpointProxyCacheRepresentation.updateDetail(collect);
        return new SumPagedRep<>(collect,
            endpoints.getTotalItemCount());
    }

    /**
     * internal query endpoints
     *
     * @param endpointIds endpoint ids
     * @return matched endpoints
     */
    public Set<Endpoint> internalQuery(Set<EndpointId> endpointIds) {
        return QueryUtility.getAllByQuery(e -> DomainRegistry.getEndpointRepository()
            .query(e), new EndpointQuery(endpointIds));
    }

    public SumPagedRep<EndpointCardRepresentation> tenantQuery(String queryParam, String pageParam,
                                                               String config) {
        EndpointQuery endpointQuery = new EndpointQuery(queryParam, pageParam, config);
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointQuery.getProjectIds(), API_MGMT);
        SumPagedRep<Endpoint> rep2 =
            DomainRegistry.getEndpointRepository().query(endpointQuery);
        return updateDetail(rep2);
    }

    private static SumPagedRep<EndpointCardRepresentation> updateDetail(
        SumPagedRep<Endpoint> rep2) {
        SumPagedRep<EndpointCardRepresentation> rep =
            new SumPagedRep<>(rep2, EndpointCardRepresentation::new);
        Set<ClientId> collect =
            rep.getData().stream().map(e -> new ClientId(e.getResourceId()))
                .collect(Collectors.toSet());
        if (!collect.isEmpty()) {
            Set<Client> allByIds = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                    new ClientQuery(collect));
            rep.getData().forEach(e -> allByIds.stream()
                .filter(ee -> ee.getClientId().getDomainId().equals(e.getResourceId())).findFirst()
                .ifPresent(ee -> {
                    e.setResourceName(ee.getName());
                }));
        }
        return rep;
    }

    public SumPagedRep<EndpointSharedCardRepresentation> marketQuery(String queryParam,
                                                                     String pageParam,
                                                                     String config) {
        EndpointQuery endpointQuery = EndpointQuery.sharedQuery(queryParam, pageParam, config);
        SumPagedRep<Endpoint> query =
            DomainRegistry.getEndpointRepository().query(endpointQuery);
        SumPagedRep<EndpointSharedCardRepresentation> rep =
            new SumPagedRep<>(query, EndpointSharedCardRepresentation::new);
        updateDetail(rep.getData());
        return rep;
    }

    public SumPagedRep<EndpointProtectedRepresentation> tenantQueryProtected(String queryParam,
                                                                             String pageParam,
                                                                             String config) {
        EndpointQuery endpointQuery =
            EndpointQuery.tenantQueryProtected(queryParam, pageParam, config);
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointQuery.getProjectIds(), API_MGMT);
        SumPagedRep<Endpoint> query =
            DomainRegistry.getEndpointRepository().query(endpointQuery);
        return new SumPagedRep<>(query, EndpointProtectedRepresentation::new);
    }

    private static void updateDetail(List<EndpointSharedCardRepresentation> original) {
        if (!original.isEmpty()) {
            Set<ClientId> collect =
                original.stream().map(EndpointSharedCardRepresentation::getClientId)
                    .collect(Collectors.toSet());
            Set<ProjectId> collect2 =
                original.stream().map(EndpointSharedCardRepresentation::getOriginalProjectId)
                    .collect(Collectors.toSet());
            Set<Client> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                    new ClientQuery(collect));
            Set<Project> allByQuery2 = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getProjectRepository().query(e),
                    new ProjectQuery(collect2));
            original.forEach(e -> {
                Optional<Client> first =
                    allByQuery.stream().filter(ee -> ee.getClientId().equals(e.getClientId()))
                        .findFirst();
                first.ifPresent(ee -> {
                    String path = ee.getPath();
                    e.setPath("/" + path + "/" + e.getPath());
                });
                Optional<Project> first2 = allByQuery2.stream()
                    .filter(ee -> ee.getProjectId().equals(e.getOriginalProjectId())).findFirst();
                first2.ifPresent(ee -> {
                    e.setProjectName(ee.getName());
                });
            });
        }
    }

    public SumPagedRep<EndpointCardRepresentation> mgmtQuery(String queryParam, String pageParam,
                                                             String config) {

        EndpointQuery endpointQuery = new EndpointQuery(queryParam, pageParam, config);
        SumPagedRep<Endpoint> rep =
            DomainRegistry.getEndpointRepository().query(endpointQuery);
        return updateDetail(rep);
    }

    public EndpointMgmtRepresentation mgmtQueryById(String id) {
        Endpoint endpoint = DomainRegistry.getEndpointRepository().get(new EndpointId(id));
        return new EndpointMgmtRepresentation(endpoint);
    }

    public Endpoint tenantQueryById(String projectId, String id) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId1, API_MGMT);
        return DomainRegistry.getEndpointRepository().get(projectId1, new EndpointId(id));
    }

    @AuditLog(actionName = CREATE_TENANT_ENDPOINT)
    public String tenantCreate(EndpointCreateCommand command,
                               String changeId) {
        EndpointId endpointId = new EndpointId();
        ProjectId projectId = new ProjectId(command.getProjectId());
        ClientId clientId = new ClientId(command.getResourceId());
        DomainRegistry.getPermissionCheckService().canAccess(projectId, API_MGMT);
        if (DomainRegistry.getEndpointRepository()
            .checkDuplicate(clientId, command.getPath(), command.getMethod())) {
            throw new DefinedRuntimeException("duplicate endpoint", "1092",
                HttpResponseCode.BAD_REQUEST);
        }
        String idempotent;
        try {
            idempotent = CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (context) -> {
                    Endpoint endpoint = Endpoint.addNewEndpoint(
                        clientId,
                        projectId,
                        command.getCacheProfileId() != null
                            ?
                            new CacheProfileId(command.getCacheProfileId()) : null,
                        command.getName(),
                        command.getDescription(),
                        command.getPath(),
                        endpointId,
                        command.getMethod(),
                        command.getSecured(),
                        command.getWebsocket(),
                        command.getCsrfEnabled(),
                        command.getCorsProfileId() != null
                            ?
                            new CorsProfileId(command.getCorsProfileId()) : null,
                        command.getShared(),
                        command.getExternal(),
                        command.getReplenishRate(),
                        command.getBurstCapacity(),
                        context
                    );
                    DomainRegistry.getEndpointRepository().add(endpoint);
                    return endpointId.getDomainId();
                }, ENDPOINT);
        } catch (DataIntegrityViolationException ex) {
            log.info(
                "unique constrain violation due to concurrent insert (no need to handle this error)",
                ex);
            throw new DefinedRuntimeException("duplicate endpoint", "1092",
                HttpResponseCode.BAD_REQUEST);
        }
        return idempotent;
    }

    @AuditLog(actionName = UPDATE_TENANT_ENDPOINT)
    public void tenantUpdate(String id, EndpointUpdateCommand command, String changeId) {
        log.debug("start of update endpoint");
        EndpointQuery endpointQuery =
            new EndpointQuery(new EndpointId(id), new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointQuery.getProjectIds(), API_MGMT);
        EndpointId endpointId = new EndpointId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Endpoint endpoint =
                    DomainRegistry.getEndpointRepository().get(endpointId);
                Endpoint update = endpoint.update(
                    command.getCacheProfileId() != null
                        ?
                        new CacheProfileId(command.getCacheProfileId()) : null,
                    command.getName(),
                    command.getDescription(),
                    command.getPath(),
                    command.getMethod(),
                    command.getCsrfEnabled(),
                    command.getCorsProfileId() != null
                        ?
                        new CorsProfileId(command.getCorsProfileId()) : null,
                    command.getReplenishRate(),
                    command.getBurstCapacity()
                );
                DomainRegistry.getEndpointRepository().update(endpoint, update);
                context.append(new EndpointCollectionModified());
                return null;
            }, ENDPOINT);
        log.debug("end of update endpoint");
    }

    @AuditLog(actionName = DELETE_TENANT_ENDPOINT)
    public void tenantRemove(String projectId, String id, String changeId) {
        EndpointQuery endpointQuery =
            new EndpointQuery(new EndpointId(id), new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointQuery.getProjectIds(), API_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Optional<Endpoint> endpoint =
                    DomainRegistry.getEndpointRepository().query(endpointQuery)
                        .findFirst();
                if (endpoint.isPresent()) {
                    Endpoint endpoint1 = endpoint.get();
                    endpoint1.remove(context);
                    DomainRegistry.getAuditService()
                        .storeAuditAction(DELETE_TENANT_ENDPOINT,
                            endpoint1);
                    DomainRegistry.getAuditService()
                        .logUserAction(log, DELETE_TENANT_ENDPOINT,
                            endpoint1);
                }
                return null;
            }, ENDPOINT);
    }

    public void reloadCache(String changeId) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                DomainRegistry.getEndpointService().reloadEndpointCache(context);
                return null;
            }, ENDPOINT);
    }

    public void expire(EndpointExpireCommand command, String projectId, String id,
                       String changeId) {
        log.debug("start of expire endpoint");
        EndpointId endpointId = new EndpointId(id);
        EndpointQuery endpointQuery =
            new EndpointQuery(endpointId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(endpointQuery.getProjectIds(), API_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Endpoint endpoint =
                    DomainRegistry.getEndpointRepository().get(endpointId);
                Endpoint expire = endpoint.expire(command.getExpireReason(), context);
                DomainRegistry.getEndpointRepository().update(endpoint, expire);
                return null;
            }, ENDPOINT);
    }

    public void handle(ClientDeleted deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getEndpointRepository().query(query),
                    new EndpointQuery(new ClientId(deserialize.getDomainId().getDomainId())));
                if (!allByQuery.isEmpty()) {
                    DomainRegistry.getEndpointRepository().remove(allByQuery);
                    context
                        .append(new EndpointCollectionModified());
                }
                return null;
            }, ENDPOINT);
    }

    public void handle(CorsProfileRemoved deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                log.debug("handle cors profile removed");
                Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getEndpointRepository().query(query),
                    new EndpointQuery(new CorsProfileId(deserialize.getDomainId().getDomainId())));
                if (!allByQuery.isEmpty()) {
                    allByQuery.forEach(e -> {
                        Endpoint endpoint = e.removeCorsRef();
                        DomainRegistry.getEndpointRepository().update(e, endpoint);
                    });
                    context
                        .append(new EndpointCollectionModified());
                }
                return null;
            }, ENDPOINT);
    }

    /**
     * refresh proxy when referred cors profile is updated.
     *
     * @param event cors profile updated event
     */
    public void handle(CorsProfileUpdated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                log.debug("handle cors profile updated");
                CorsProfileId corsProfileId =
                    new CorsProfileId(event.getDomainId().getDomainId());
                SumPagedRep<Endpoint> endpointSumPagedRep = DomainRegistry.getEndpointRepository()
                    .query(new EndpointQuery(corsProfileId));
                if (endpointSumPagedRep.findFirst().isPresent()) {
                    context
                        .append(new EndpointCollectionModified());
                }
                return null;
            }, ENDPOINT);
    }

    public void handle(CacheProfileRemoved deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                log.debug("handle cache profile removed");
                CacheProfileId profileId =
                    new CacheProfileId(deserialize.getDomainId().getDomainId());
                Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getEndpointRepository().query(query),
                    new EndpointQuery(profileId));
                if (!allByQuery.isEmpty()) {
                    context
                        .append(new EndpointCollectionModified());
                    allByQuery.forEach(e -> {
                        Endpoint endpoint = e.removeCacheProfileRef();
                        DomainRegistry.getEndpointRepository().update(e, endpoint);
                    });
                }
                return null;
            }, ENDPOINT);
    }

    public void handle(CacheProfileUpdated deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (context) -> {
                log.debug("handle cache profile updated");
                CacheProfileId profileId =
                    new CacheProfileId(deserialize.getDomainId().getDomainId());
                SumPagedRep<Endpoint> firstPage = DomainRegistry.getEndpointRepository()
                    .query(new EndpointQuery(profileId));
                if (firstPage.findFirst().isPresent()) {
                    context
                        .append(new EndpointCollectionModified());
                } else {
                    log.debug("cache profile is not used");
                }
                return null;
            }, ENDPOINT);
    }

}
