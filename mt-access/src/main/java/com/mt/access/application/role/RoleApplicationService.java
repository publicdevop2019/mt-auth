package com.mt.access.application.role;

import static com.mt.access.domain.model.audit.AuditActionName.CREATE_TENANT_ROLE;
import static com.mt.access.domain.model.audit.AuditActionName.DELETE_TENANT_ROLE;
import static com.mt.access.domain.model.audit.AuditActionName.PATCH_TENANT_ROLE;
import static com.mt.access.domain.model.audit.AuditActionName.UPDATE_TENANT_ROLE;
import static com.mt.access.domain.model.permission.Permission.CREATE_ROLE;
import static com.mt.access.domain.model.permission.Permission.EDIT_ROLE;
import static com.mt.access.domain.model.permission.Permission.VIEW_ROLE;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.role.command.RoleCreateCommand;
import com.mt.access.application.role.command.RolePatchCommand;
import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleType;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.SagaDistLock;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleApplicationService {
    private static final String ROLE = "Role";

    public SumPagedRep<Role> query(RoleQuery roleQuery) {
        return DomainRegistry.getRoleRepository().query(roleQuery);
    }

    public SumPagedRep<Role> query(String queryParam, String pageParam, String skipCount) {
        RoleQuery roleQuery = new RoleQuery(queryParam, pageParam, skipCount);
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), VIEW_ROLE);
        return DomainRegistry.getRoleRepository().query(roleQuery);
    }

    public Role query(String projectId, String id) {
        ProjectId projectId1 = new ProjectId(projectId);
        DomainRegistry.getPermissionCheckService().canAccess(projectId1, VIEW_ROLE);
        return DomainRegistry.getRoleRepository().get(projectId1,new RoleId(id));
    }

    public Role internalQueryById(RoleId id) {
        return DomainRegistry.getRoleRepository().get(id);
    }


    @AuditLog(actionName = UPDATE_TENANT_ROLE)
    public void tenantUpdate(String id, RoleUpdateCommand command, String changeId) {
        RoleId roleId = new RoleId(id);
        RoleQuery roleQuery = new RoleQuery(roleId, new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), EDIT_ROLE);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Role> first =
                    DomainRegistry.getRoleRepository().query(roleQuery).findFirst();
                first.ifPresent(e -> {
                    e.update(command);
                    DomainRegistry.getRoleRepository().add(e);
                });
                return null;
            }, ROLE);
    }

    @AuditLog(actionName = PATCH_TENANT_ROLE)
    public void tenantPatch(String projectId, String id, JsonPatch command, String changeId) {
        RoleId roleId = new RoleId(id);
        RoleQuery roleQuery = new RoleQuery(roleId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), EDIT_ROLE);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Optional<Role> first =
                    DomainRegistry.getRoleRepository().query(roleQuery).findFirst();
                first.ifPresent(e -> {
                    RolePatchCommand beforePatch = new RolePatchCommand(e);
                    RolePatchCommand afterPatch =
                        CommonDomainRegistry.getCustomObjectSerializer()
                            .applyJsonPatch(command, beforePatch, RolePatchCommand.class);
                    e.patch(afterPatch.getName());
                    DomainRegistry.getRoleRepository().add(e);
                });
                return null;
            }, ROLE);
    }


    @AuditLog(actionName = DELETE_TENANT_ROLE)
    public void tenantRemove(String projectId, String id, String changeId) {
        RoleId roleId = new RoleId(id);
        RoleQuery roleQuery = new RoleQuery(roleId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), EDIT_ROLE);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Role role = DomainRegistry.getRoleRepository().get(roleId);
            role.remove();
            DomainRegistry.getAuditService()
                .storeAuditAction(DELETE_TENANT_ROLE,
                    role);
            DomainRegistry.getAuditService()
                .logUserAction(log, DELETE_TENANT_ROLE,
                    role);
            return null;
        }, ROLE);
    }

    /**
     * create role, permissions must belong to root node.
     *
     * @param command  create command
     * @param changeId change id
     * @return role created id
     */

    @AuditLog(actionName = CREATE_TENANT_ROLE)
    public String tenantCreate(RoleCreateCommand command, String changeId) {
        RoleId roleId = new RoleId();
        DomainRegistry.getPermissionCheckService()
            .canAccess(new ProjectId(command.getProjectId()), CREATE_ROLE);
        return CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (change) -> {
                Role role = Role.createRoleForTenant(
                    new ProjectId(command.getProjectId()),
                    roleId,
                    command.getName(),
                    command.getDescription(),
                    command.getCommonPermissionIds().stream().map(PermissionId::new)
                        .collect(Collectors.toSet()),
                    command.getApiPermissionIds().stream().map(PermissionId::new)
                        .collect(Collectors.toSet()),
                    RoleType.USER,
                    command.getParentId() != null ? new RoleId(command.getParentId()) : null,
                    command.getExternalPermissionIds() != null
                        ?
                        command.getExternalPermissionIds().stream().map(PermissionId::new)
                            .collect(Collectors.toSet()) : null
                );
                DomainRegistry.getRoleRepository().add(role);
                return roleId.getDomainId();
            }, ROLE);
    }

    /**
     * create admin role to mt-auth and default user role to target project.
     *
     * @param event permission created event
     */
    public void handle(ProjectPermissionCreated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(event.getId().toString(), (ignored) -> {
                log.debug("handle project permission created event");
                ProjectId tenantProjectId = event.getProjectId();
                ProjectId authPId = new ProjectId(AppConstant.MT_AUTH_PROJECT_ID);
                UserId creator = event.getCreator();
                Set<PermissionId> permissionIdSet =
                    event.getDomainIds().stream().map(e -> new PermissionId(e.getDomainId()))
                        .collect(Collectors.toSet());
                Role.onboardNewProject(authPId, tenantProjectId, permissionIdSet, creator);
                return null;
            }, ROLE);
    }

    /**
     * create placeholder role when new client created,
     * use saga lock to make sure event get consumed correctly.
     * e.g client deleted consumed first then client created consumed next
     *
     * @param event client created event
     */
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = ROLE, unlockAfter = 2)
    public void handle(ClientCreated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotentMsg(event.getChangeId(), (ignored) -> {
                ProjectId projectId = event.getProjectId();
                ClientId clientId = new ClientId(event.getDomainId().getDomainId());
                RoleId roleId = event.getRoleId();
                log.trace("before get project root role");
                Set<Role> allByQuery = QueryUtility
                    .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                        RoleQuery.getRootRole(projectId));
                log.trace("get project root role");
                Optional<Role> first =
                    allByQuery.stream().filter(e -> RoleType.CLIENT_ROOT.equals(e.getType()))
                        .findFirst();
                if (first.isEmpty()) {
                    throw new DefinedRuntimeException("unable to find root client role", "1019",
                        HttpResponseCode.NOT_HTTP);
                }
                Role userRole = Role.newClient(projectId, roleId, clientId.getDomainId(),
                    first.get().getRoleId());
                log.trace("create user role");
                DomainRegistry.getRoleRepository().add(userRole);
                return null;
            }, (cmd) -> null, ROLE);
    }


    /**
     * clean up role after client delete,
     * use saga lock to make sure event get consumed correctly.
     * e.g client deleted consumed first then client created consumed next
     *
     * @param event clientDeleted event
     */
    @SagaDistLock(keyExpression = "#p0.changeId", aggregateName = ROLE, unlockAfter = 2)
    public void handle(ClientDeleted event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotentMsg(event.getChangeId(), (ignored) -> {
                log.debug("handle client removed event {}", event.getDomainId().getDomainId());
                ClientId clientId = new ClientId(event.getDomainId().getDomainId());
                RoleQuery roleQuery = RoleQuery.forClientId(clientId);
                Set<Role> allByQuery = QueryUtility
                    .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                        roleQuery);
                log.debug("role to be removed {}", allByQuery.size());
                allByQuery.forEach(e -> DomainRegistry.getRoleRepository().remove(e));
                return null;
            }, (cmd) -> null, ROLE);
    }

}
