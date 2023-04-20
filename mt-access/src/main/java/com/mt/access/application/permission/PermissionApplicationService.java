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
import com.mt.access.domain.model.endpoint.EndpointQuery;
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
     * @param queryParam query string
     * @param pageParam  page config
     * @return paged permission
     */
    public SumPagedRep<Permission> sharedQuery(String queryParam, String pageParam) {
        Set<EndpointId> endpointIds = ApplicationServiceRegistry.getSubRequestApplicationService()
            .internalSubscribedEndpointIds();
        if (!endpointIds.isEmpty()) {
            Set<Endpoint> endpoints =
                ApplicationServiceRegistry.getEndpointApplicationService()
                    .internalQuery(endpointIds);
            Set<PermissionId> subPermissionIds =
                endpoints.stream().map(Endpoint::getPermissionId)
                    .filter(Objects::nonNull)//filter shared endpoint that has no permission check
                    .collect(Collectors.toSet());
            PermissionQuery permissionQuery =
                PermissionQuery.subscribeSharedQuery(subPermissionIds, queryParam, pageParam);
            return DomainRegistry.getPermissionRepository().getByQuery(permissionQuery);
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
            .getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery(e),
                PermissionQuery.uiPermissionQuery(tenantIds, reservedUIPermissionName));
    }

    public SumPagedRep<Permission> tenantQuery(String queryParam, String pageParam,
                                               String skipCount) {
        PermissionQuery permissionQuery = new PermissionQuery(queryParam, pageParam, skipCount);
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), VIEW_PERMISSION);
        return DomainRegistry.getPermissionRepository().getByQuery(permissionQuery);
    }

    public Optional<Permission> tenantQuery(String projectId, String id) {
        PermissionQuery permissionQuery =
            new PermissionQuery(new PermissionId(id), new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService()
            .canAccess(permissionQuery.getProjectIds(), VIEW_PERMISSION);
        return DomainRegistry.getPermissionRepository().getByQuery(permissionQuery).findFirst();
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
                    DomainRegistry.getPermissionRepository().getByQuery(permissionQuery)
                        .findFirst();
                first.ifPresent(ee -> {
                    Set<PermissionId> linkedPermId = null;
                    if (command.getLinkedApiIds() != null && !command.getLinkedApiIds().isEmpty()) {
                        Set<EndpointId> collect =
                            command.getLinkedApiIds().stream().map(EndpointId::new)
                                .collect(Collectors.toSet());
                        Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
                            e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e),
                            new EndpointQuery(collect));
                        Validator.equalTo(allByQuery.size(), collect.size(),
                            "unable to find all endpoint");
                        linkedPermId = allByQuery.stream().map(Endpoint::getPermissionId)
                            .collect(Collectors.toSet());
                    }
                    ee.replace(command.getName(), linkedPermId);
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
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Permission> permission =
                DomainRegistry.getPermissionRepository().getByQuery(permissionQuery).findFirst();
            permission.ifPresent(e -> {
                e.remove();
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
            .idempotent(changeId, (ignored) -> {
                Optional<Permission> permission =
                    DomainRegistry.getPermissionRepository().getByQuery(permissionQuery)
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
        DomainRegistry.getPermissionCheckService()
            .canAccess(new ProjectId(command.getProjectId()), CREATE_PERMISSION);
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Set<PermissionId> linkedPermId = null;
                if (command.getLinkedApiIds() != null && !command.getLinkedApiIds().isEmpty()) {
                    Set<EndpointId> collect =
                        command.getLinkedApiIds().stream().map(EndpointId::new)
                            .collect(Collectors.toSet());
                    Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(
                        e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e),
                        new EndpointQuery(collect));
                    Validator
                        .equalTo(allByQuery.size(), collect.size(), "unable to find all endpoint");
                    linkedPermId = allByQuery.stream().map(Endpoint::getPermissionId)
                        .collect(Collectors.toSet());
                }
                Permission permission;
                if (command.getParentId() != null && !command.getParentId().isBlank()) {
                    permission = Permission
                        .manualCreate(new ProjectId(command.getProjectId()), permissionId,
                            command.getName(), PermissionType.COMMON,
                            new PermissionId(command.getParentId()), null, linkedPermId);
                } else {
                    permission = Permission
                        .manualCreate(new ProjectId(command.getProjectId()), permissionId,
                            command.getName(), PermissionType.COMMON, null, null, linkedPermId);
                }
                DomainRegistry.getPermissionRepository().add(permission);
                return permissionId.getDomainId();
            }, PERMISSION);
    }


    public void handle(StartNewProjectOnboarding event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("handle project created event");
                ProjectId tenantProjectId = new ProjectId(event.getDomainId().getDomainId());
                Permission.onboardNewProject(tenantProjectId, event.getCreator());
                return null;
            }, PERMISSION);
    }


    public void handle(SecureEndpointCreated deserialize) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(deserialize.getId().toString(), (ignored) -> {
                log.debug("handle endpoint created event");
                EndpointId endpointId = new EndpointId(deserialize.getDomainId().getDomainId());
                PermissionId permissionId = deserialize.getPermissionId();
                ProjectId projectId = deserialize.getProjectId();
                Permission
                    .addNewEndpoint(projectId, endpointId, permissionId, deserialize.isShared());
                return null;
            }, PERMISSION);
    }

    /**
     * remove permission after secure endpoint removed.
     *
     * @param event SecureEndpointRemoved event
     */
    public void handle(SecureEndpointRemoved event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("handle secured endpoint remove event");
                Set<PermissionId> permissionIds = event.getPermissionIds();
                PermissionQuery permissionQuery = new PermissionQuery(permissionIds);
                Set<Permission> allByQuery = QueryUtility
                    .getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery(e),
                        permissionQuery);
                Validator.sizeEqualTo(permissionIds, allByQuery,
                    "unable to find all permission for deleted endpoints");
                DomainRegistry.getPermissionRepository().removeAll(allByQuery);
                return null;
            }, PERMISSION);
    }


}
