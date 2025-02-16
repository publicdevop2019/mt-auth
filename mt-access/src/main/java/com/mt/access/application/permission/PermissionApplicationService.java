package com.mt.access.application.permission;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_PERMISSION;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_PERMISSION;
import static com.mt.access.domain.model.permission.Permission.PERMISSION_MGMT;
import static com.mt.access.domain.model.permission.Permission.reservedUIPermissionName;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.representation.UiPermissionInfo;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved;
import com.mt.access.domain.model.permission.LinkedApiPermissionId;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.distributed_lock.SagaDistLockV2;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PermissionApplicationService {
    private static final String PERMISSION = "Permission";

    /**
     * get subscribed endpoint permissions
     *
     * @param rawProjectId raw project id
     * @param queryParam   query string
     * @param pageParam    page config
     * @return paged permission
     */
    public SumPagedRep<Permission> sharedQuery(String rawProjectId, String queryParam,
                                               String pageParam) {
        //get subscribed endpoints
        ProjectId projectId = new ProjectId(rawProjectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, PERMISSION_MGMT);
        Set<EndpointId> endpointIds = ApplicationServiceRegistry.getSubRequestApplicationService()
            .internalSubscribedEndpointIds(projectId);
        if (!endpointIds.isEmpty()) {
            Set<Endpoint> endpoints =
                ApplicationServiceRegistry.getEndpointApplicationService()
                    .internalQuery(endpointIds);
            //get endpoint's permission
            Set<PermissionId> subPermissionIds =
                endpoints.stream().map(Endpoint::getPermissionId)
                    .filter(Objects::nonNull)//filter shared endpoint that has no permission check
                    .collect(Collectors.toSet());
            PermissionQuery permissionQuery =
                PermissionQuery.subscribeSharedQuery(subPermissionIds, queryParam, pageParam);
            return DomainRegistry.getPermissionRepository().query(permissionQuery);
        } else {
            return SumPagedRep.empty();
        }
    }

    /**
     * get permissions for ui usage.
     *
     * @return permission set
     */
    public UiPermissionInfo uiQuery(String rawProjectId) {
        ProjectId projectId = new ProjectId(rawProjectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId);
        SumPagedRep<Permission> query =
            DomainRegistry.getPermissionRepository()
                .query(PermissionQuery.uiPermissionQuery(projectId, reservedUIPermissionName));
        return new UiPermissionInfo(query.getData());
    }

    public SumPagedRep<Permission> tenantQuery(String queryParam, String pageParam,
                                               String skipCount) {
        PermissionQuery permissionQuery = new PermissionQuery(queryParam, pageParam, skipCount);
        Validator.notNull(permissionQuery.getProjectIds());
        Validator.notEmpty(permissionQuery.getProjectIds());
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), PERMISSION_MGMT);
        return DomainRegistry.getPermissionRepository().query(permissionQuery);
    }

    @AuditLog(actionName = DELETE_TENANT_PERMISSION)
    public void tenantRemove(String projectId, String id, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery =
            PermissionQuery.tenantQuery(new ProjectId(projectId), permissionId);
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), PERMISSION_MGMT);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
            Optional<Permission> permission =
                DomainRegistry.getPermissionRepository().query(permissionQuery).findFirst();
            permission.ifPresent(e -> {
                e.remove(context);
                DomainRegistry.getAuditService()
                    .storeAuditAction(DELETE_TENANT_PERMISSION,
                        e);
                DomainRegistry.getAuditService()
                    .logUserAction(log, DELETE_TENANT_PERMISSION,
                        e);
            });
            return null;
        }, PERMISSION);
    }

    @AuditLog(actionName = CREATE_TENANT_PERMISSION)
    public String tenantCreate(PermissionCreateCommand command, String changeId) {
        PermissionId permissionId = new PermissionId();
        ProjectId projectId = new ProjectId(command.getProjectId());
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId, PERMISSION_MGMT);
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Set<PermissionId> linkedPermId =
                    DomainRegistry.getPermissionService()
                        .tenantFindPermissionIds(command.getLinkedApiIds(),
                            Collections.singleton(projectId));
                Permission permission = Permission
                    .manualCreate(new ProjectId(command.getProjectId()), permissionId,
                        command.getName(), command.getDescription(), PermissionType.COMMON,
                        null);
                DomainRegistry.getPermissionRepository().add(permission);
                LinkedApiPermissionId.add(permission, linkedPermId);
                return permissionId.getDomainId();
            }, PERMISSION);
    }

    public void handle(StartNewProjectOnboarding event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                ProjectId tenantProjectId = new ProjectId(event.getDomainId().getDomainId());
                log.info("handle new project created event, project id {}",
                    tenantProjectId.getDomainId());
                Permission.onboardNewProject(tenantProjectId, event.getCreator(), context);
                return null;
            }, PERMISSION);
    }

    @SagaDistLockV2(keyExpression = "#p0.changeId", aggregateName = PERMISSION)
    public void handle(SecureEndpointCreated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                log.debug("handle endpoint created event with permission id {}",
                    event.getPermissionId().getDomainId());
                EndpointId endpointId = new EndpointId(event.getDomainId().getDomainId());
                PermissionId permissionId = event.getPermissionId();
                ProjectId projectId = event.getProjectId();
                Permission
                    .addNewEndpoint(projectId, endpointId, permissionId, event.getShared());
                return null;
            }, PERMISSION);
    }

    /**
     * remove permission after secure endpoint removed.
     *
     * @param event SecureEndpointRemoved event
     */
    @SagaDistLockV2(keyExpression = "#p0.changeId", aggregateName = PERMISSION)
    public void handle(SecureEndpointRemoved event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                log.debug("handle secured endpoint remove event, permission id {}",
                    event.getPermissionId().getDomainId());
                DomainRegistry.getPermissionService()
                    .cleanRelated(event.getPermissionId(), context);
                return null;
            }, PERMISSION);
    }
}
