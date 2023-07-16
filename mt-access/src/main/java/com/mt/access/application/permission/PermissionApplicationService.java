package com.mt.access.application.permission;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_PERMISSION;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_PERMISSION;
import static com.mt.access.domain.model.audit.AuditActionName.PATCH_TENANT_PERMISSION;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_PERMISSION;
import static com.mt.access.domain.model.permission.Permission.CREATE_PERMISSION;
import static com.mt.access.domain.model.permission.Permission.EDIT_PERMISSION;
import static com.mt.access.domain.model.permission.Permission.VIEW_PERMISSION;
import static com.mt.access.domain.model.permission.Permission.reservedUIPermissionName;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.command.PermissionPatchCommand;
import com.mt.access.application.permission.command.PermissionUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.endpoint.event.SecureEndpointRemoved;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.event.StartNewProjectOnboarding;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.develop.RecordElapseTime;
import com.mt.common.domain.model.distributed_lock.SagaDistLockV2;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
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
     * @param rawProjectId
     * @param queryParam   query string
     * @param pageParam    page config
     * @return paged permission
     */
    public SumPagedRep<Permission> sharedQuery(String rawProjectId, String queryParam,
                                               String pageParam) {
        //get subscribed endpoints
        ProjectId projectId = new ProjectId(rawProjectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId, VIEW_PERMISSION);
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
    public Set<Permission> uiQuery() {
        Set<ProjectId> tenantIds = DomainRegistry.getCurrentUserService().getTenantIds();
        if (tenantIds.isEmpty()) {
            return Collections.emptySet();
        }
        return QueryUtility
            .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                PermissionQuery.uiPermissionQuery(tenantIds, reservedUIPermissionName));
    }

    public SumPagedRep<Permission> tenantQuery(String queryParam, String pageParam,
                                               String skipCount) {
        PermissionQuery permissionQuery = new PermissionQuery(queryParam, pageParam, skipCount);
        Validator.notNull(permissionQuery.getProjectIds());
        Validator.notEmpty(permissionQuery.getProjectIds());
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), VIEW_PERMISSION);
        return DomainRegistry.getPermissionRepository().query(permissionQuery);
    }

    public Permission tenantQueryById(String projectId, String id) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId1, VIEW_PERMISSION);
        return DomainRegistry.getPermissionRepository().get(projectId1, new PermissionId(id));
    }


    @AuditLog(actionName = UPDATE_TENANT_PERMISSION)
    public void tenantUpdate(String id, PermissionUpdateCommand command, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery =
            new PermissionQuery(permissionId, new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), EDIT_PERMISSION);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Permission> first =
                    DomainRegistry.getPermissionRepository().query(permissionQuery)
                        .findFirst();
                first.ifPresent(ee -> {
                    Set<PermissionId> permissionIds =
                        DomainRegistry.getPermissionService()
                            .tenantFindPermissionIds(command.getLinkedApiIds(),
                            permissionQuery.getProjectIds());
                    ee.replace(command.getName(), permissionIds);
                    DomainRegistry.getPermissionRepository().add(ee);
                });
                return null;
            }, PERMISSION);
    }


    @AuditLog(actionName = DELETE_TENANT_PERMISSION)
    public void tenantRemove(String projectId, String id, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery =
            new PermissionQuery(permissionId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), EDIT_PERMISSION);
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


    @AuditLog(actionName = PATCH_TENANT_PERMISSION)
    public void tenantPatch(String projectId, String id, JsonPatch command, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery =
            new PermissionQuery(permissionId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), EDIT_PERMISSION);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Optional<Permission> permission =
                    DomainRegistry.getPermissionRepository().query(permissionQuery)
                        .findFirst();
                if (permission.isPresent()) {
                    Permission permission1 = permission.get();
                    PermissionPatchCommand beforePatch = new PermissionPatchCommand(permission1);
                    PermissionPatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, PermissionPatchCommand.class);
                    permission1.patch(
                        afterPatch.getName()
                    );
                }
                return null;
            }, PERMISSION);
    }


    @AuditLog(actionName = CREATE_TENANT_PERMISSION)
    public String tenantCreate(PermissionCreateCommand command, String changeId) {
        PermissionId permissionId = new PermissionId();
        ProjectId projectId = new ProjectId(command.getProjectId());
        DomainRegistry.getPermissionCheckService()
            .canAccess(projectId, CREATE_PERMISSION);
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Set<PermissionId> linkedPermId =
                    DomainRegistry.getPermissionService()
                        .tenantFindPermissionIds(command.getLinkedApiIds(),
                            Collections.singleton(projectId));
                Permission permission = Permission
                    .manualCreate(new ProjectId(command.getProjectId()), permissionId,
                        command.getName(), PermissionType.COMMON,
                        command.getParentId() != null ? new PermissionId(command.getParentId()) :
                            null, null, linkedPermId);
                DomainRegistry.getPermissionRepository().add(permission);
                return permissionId.getDomainId();
            }, PERMISSION);
    }

    @RecordElapseTime
    public void handle(StartNewProjectOnboarding event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                log.debug("handle new project created event");
                ProjectId tenantProjectId = new ProjectId(event.getDomainId().getDomainId());
                Permission.onboardNewProject(tenantProjectId, event.getCreator(),context);
                return null;
            }, PERMISSION);
    }

    @SagaDistLockV2(keyExpression = "#p0.changeId", aggregateName = PERMISSION)
    public void handle(SecureEndpointCreated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (context) -> {
                log.debug("handle endpoint created event");
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
                log.debug("handle secured endpoint remove event");
                DomainRegistry.getPermissionService().cleanRelated(event.getPermissionId(),context);
                return null;
            }, PERMISSION);
    }
}
