package com.mt.access.application.endpoint;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_ROUTER;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_ROUTER;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_ROUTER;
import static com.mt.access.domain.model.permission.Permission.API_MGMT;

import com.mt.access.application.endpoint.command.RouterCreateCommand;
import com.mt.access.application.endpoint.command.RouterUpdateCommand;
import com.mt.access.application.endpoint.representation.RouterRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.endpoint.ExternalUrl;
import com.mt.access.domain.model.endpoint.Router;
import com.mt.access.domain.model.endpoint.RouterId;
import com.mt.access.domain.model.endpoint.RouterQuery;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RouterApplicationService {
    private static final String ROUTER = "ROUTER";

    @AuditLog(actionName = CREATE_TENANT_ROUTER)
    public String tenantCreate(RouterCreateCommand command,
                               String changeId) {
        RouterId routerId = new RouterId();
        ProjectId projectId = new ProjectId(command.getProjectId());
        DomainRegistry.getPermissionCheckService().canAccess(projectId, API_MGMT);
        String idempotent;
        try {
            idempotent = CommonApplicationServiceRegistry.getIdempotentService()
                .idempotent(changeId, (context) -> {
                    Router router = Router.addNewRouter(
                        projectId,
                        command.getName(),
                        command.getDescription(),
                        command.getPath(),
                        routerId,
                        new ExternalUrl(command.getExternalUrl()),
                        context
                    );
                    DomainRegistry.getRouterRepository().add(router);
                    return routerId.getDomainId();
                }, ROUTER);
        } catch (DataIntegrityViolationException ex) {
            log.info("unique constrain violation (no need to handle this error)", ex);
            throw new DefinedRuntimeException("duplicate router", "1100",
                HttpResponseCode.BAD_REQUEST);
        }
        return idempotent;
    }

    @AuditLog(actionName = UPDATE_TENANT_ROUTER)
    public void tenantUpdate(String id, RouterUpdateCommand command, String changeId) {
        log.debug("start of update router");
        RouterQuery query =
            new RouterQuery(new RouterId(id), new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService().canAccess(query.getProjectIds(), API_MGMT);
        RouterId routerId = new RouterId(id);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Router router =
                    DomainRegistry.getRouterRepository().get(routerId);
                Router update = router.update(
                    command.getName(),
                    command.getDescription(),
                    command.getPath(),
                    new ExternalUrl(command.getExternalUrl()),
                    context
                );
                DomainRegistry.getRouterRepository().update(router, update);
                return null;
            }, ROUTER);
        log.debug("end of update router");
    }

    @AuditLog(actionName = DELETE_TENANT_ROUTER)
    public void tenantRemove(String projectId, String id, String changeId) {
        RouterQuery query =
            new RouterQuery(new RouterId(id), new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(query.getProjectIds(), API_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Optional<Router> optionalRouter =
                    DomainRegistry.getRouterRepository().query(query)
                        .findFirst();
                if (optionalRouter.isPresent()) {
                    Router router = optionalRouter.get();
                    router.removeIfNoEndpoints();
                    DomainRegistry.getAuditService()
                        .storeAuditAction(DELETE_TENANT_ROUTER,
                            router);
                    DomainRegistry.getAuditService()
                        .logUserAction(log, DELETE_TENANT_ROUTER,
                            router);
                }
                return null;
            }, ROUTER);
    }

    public SumPagedRep<RouterRepresentation> tenantQuery(String projectId1, String pageParam,
                                                         String config) {
        ProjectId projectId = new ProjectId(projectId1);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, API_MGMT);
        SumPagedRep<Router> query = DomainRegistry.getRouterRepository()
            .query(new RouterQuery(projectId, pageParam, config));
        return new SumPagedRep<>(query, RouterRepresentation::new);
    }

    public SumPagedRep<RouterRepresentation> internalProxyQuery(String pageParam, String config) {
        SumPagedRep<Router> query = DomainRegistry.getRouterRepository()
            .query(new RouterQuery(pageParam, config));
        return new SumPagedRep<>(query, RouterRepresentation::new);
    }

    public SumPagedRep<RouterRepresentation> mgmtQuery(String pageParam, String config) {
        SumPagedRep<Router> query = DomainRegistry.getRouterRepository()
            .query(new RouterQuery(pageParam, config));
        return new SumPagedRep<>(query, RouterRepresentation::new);
    }
}
