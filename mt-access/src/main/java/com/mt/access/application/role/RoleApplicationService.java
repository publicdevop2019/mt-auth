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
import com.mt.access.application.role.representation.RoleCardRepresentation;
import com.mt.access.application.role.representation.RoleRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.audit.AuditLog;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.ClientQuery;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.client.event.ClientDeleted;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.event.PermissionRemoved;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.Project;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.ProjectQuery;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleType;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.distributed_lock.SagaDistLockV2;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.infrastructure.CommonUtility;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoleApplicationService {
    private static final String ROLE = "Role";

    public SumPagedRep<Role> query(RoleQuery roleQuery) {
        return DomainRegistry.getRoleRepository().query(roleQuery);
    }

    public SumPagedRep<RoleCardRepresentation> query(String queryParam, String pageParam,
                                                     String skipCount) {
        return CommonDomainRegistry.getTransactionService()
            .returnedTransactionalEvent((context) -> {

                RoleQuery roleQuery = new RoleQuery(queryParam, pageParam, skipCount);
                DomainRegistry.getPermissionCheckService()
                    .canAccess(roleQuery.getProjectIds(), VIEW_ROLE);
                SumPagedRep<Role> query = DomainRegistry.getRoleRepository().query(roleQuery);
                SumPagedRep<RoleCardRepresentation> roleCardRepresentationSumPagedRep =
                    new SumPagedRep<>(query, RoleCardRepresentation::new);
                updateName(roleCardRepresentationSumPagedRep);
                return roleCardRepresentationSumPagedRep;
            });
    }

    public RoleRepresentation query(String projectId, String id) {
        return CommonDomainRegistry.getTransactionService().returnedTransactionalEvent((context) -> {
            ProjectId projectId1 = new ProjectId(projectId);
            DomainRegistry.getPermissionCheckService().canAccess(projectId1, VIEW_ROLE);
            Role role = DomainRegistry.getRoleRepository().get(projectId1, new RoleId(id));
            return new RoleRepresentation(role);
        });
    }


    @AuditLog(actionName = UPDATE_TENANT_ROLE)
    public void tenantUpdate(String id, RoleUpdateCommand command, String changeId) {
        RoleId roleId = new RoleId(id);
        RoleQuery roleQuery = new RoleQuery(roleId, new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), EDIT_ROLE);
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotent(changeId, (context) -> {
                Optional<Role> first =
                    DomainRegistry.getRoleRepository().query(roleQuery).findFirst();
                first.ifPresent(e -> {
                    e.replace(command, context);
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
                    e.patch(afterPatch.getName(), afterPatch.getDescription());
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
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (context) -> {
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
            .idempotent(changeId, (context) -> {
                Role role = Role.createRoleForTenant(
                    new ProjectId(command.getProjectId()),
                    roleId,
                    command.getName(),
                    command.getDescription(),
                    CommonUtility.map(command.getCommonPermissionIds(), PermissionId::new),
                    CommonUtility.map(command.getApiPermissionIds(), PermissionId::new),
                    command.getParentId() == null ? null : new RoleId(command.getParentId()),
                    CommonUtility.map(command.getExternalPermissionIds(), PermissionId::new),
                    context
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
            .idempotent(event.getId().toString(), (context) -> {
                ProjectId tenantProjectId = event.getProjectId();
                log.info("handle new project permission created event, project id {}",
                    tenantProjectId.getDomainId());
                ProjectId authPId = new ProjectId(AppConstant.MT_AUTH_PROJECT_ID);
                UserId creator = event.getCreator();
                Role.onboardNewProject(authPId, tenantProjectId, event.getCommonPermissionIds(),
                    event.getLinkedPermissionIds(), creator, context);
                return null;
            }, ROLE);
    }

    /**
     * remove roles refer to deleted permissions
     *
     * @param event permission remove event
     */
    public void handle(PermissionRemoved event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotentMsg(event.getId().toString(), (context) -> {
                log.debug("handle permission removed event");
                PermissionId permissionId = new PermissionId(event.getDomainId().getDomainId());
                Set<Role> allByQuery = QueryUtility.getAllByQuery(
                    (query) -> DomainRegistry.getRoleRepository().query(query),
                    RoleQuery.referredPermissions(permissionId));
                allByQuery.forEach(e -> e.removePermission(permissionId));
                return null;
            }, (cmd) -> null, ROLE);
    }

    /**
     * create placeholder role when new client created,
     * use saga lock to make sure event get consumed correctly.
     * e.g client deleted consumed first then client created consumed next
     *
     * @param event client created event
     */
    @SagaDistLockV2(keyExpression = "#p0.changeId", aggregateName = ROLE)
    public void handle(ClientCreated event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotentMsg(event.getChangeId(), (context) -> {
                log.info("handling client created event with id {}",
                    event.getDomainId().getDomainId());
                ProjectId projectId = event.getProjectId();
                ClientId clientId = new ClientId(event.getDomainId().getDomainId());
                RoleId roleId = event.getRoleId();
                log.trace("get project root role");
                Optional<Role> first =
                    DomainRegistry.getRoleRepository().queryClientRoot(projectId);
                if (first.isEmpty()) {
                    throw new DefinedRuntimeException("unable to find root client role", "1019",
                        HttpResponseCode.NOT_HTTP);
                }
                Role clientRole = Role.newClient(projectId, roleId, clientId.getDomainId(),
                    first.get().getRoleId());
                log.trace("create client role");
                DomainRegistry.getRoleRepository().add(clientRole);
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
    @SagaDistLockV2(keyExpression = "#p0.changeId", aggregateName = ROLE)
    public void handle(ClientDeleted event) {
        CommonApplicationServiceRegistry.getIdempotentService()
            .idempotentMsg(event.getChangeId(), (context) -> {
                log.info("handle client removed event {}", event.getDomainId().getDomainId());
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

    public static SumPagedRep<RoleCardRepresentation> updateName(
        SumPagedRep<RoleCardRepresentation> response) {
        List<RoleCardRepresentation> data = response.getData();
        Set<ProjectId> collect =
            data.stream().filter(e -> e.getRoleType().equals(RoleType.PROJECT)).flatMap(e -> {
                if (e.getTenantId() != null) {
                    return Stream.of(new ProjectId(e.getName()), new ProjectId(e.getTenantId()));
                }
                return Stream.of(new ProjectId(e.getName()));
            }).collect(Collectors.toSet());
        Set<ProjectId> collect2 =
            data.stream().filter(e -> e.getTenantId() != null)
                .map(e -> new ProjectId(e.getTenantId()))
                .collect(Collectors.toSet());
        collect.addAll(collect2);
        if (collect.size() > 0) {
            Set<Project> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getProjectRepository().query(e),
                    new ProjectQuery(collect));
            data.forEach(e -> {
                if (e.getRoleType().equals(RoleType.PROJECT)) {
                    allByQuery.stream()
                        .filter(ee -> ee.getProjectId().getDomainId().equals(e.getName()))
                        .findFirst().ifPresent(ee -> e.setName(ee.getName()));
                }
                if (e.getTenantId() != null) {
                    allByQuery.stream()
                        .filter(ee2 -> ee2.getProjectId().getDomainId().equals(e.getTenantId()))
                        .findFirst().ifPresent(eee -> e.setTenantId(eee.getName()));
                }
            });
        }
        Set<ClientId> collect1 = data.stream().filter(e -> e.getRoleType().equals(RoleType.CLIENT))
            .map(e -> new ClientId(e.getName())).collect(Collectors.toSet());
        if (collect1.size() > 0) {
            Set<Client> allByQuery2 = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getClientRepository().query(e),
                    new ClientQuery(collect1));
            data.forEach(e -> {
                if (e.getRoleType().equals(RoleType.CLIENT)) {
                    allByQuery2.stream()
                        .filter(ee -> ee.getClientId().getDomainId().equals(e.getName()))
                        .findFirst().ifPresent(ee -> e.setName(ee.getName()));
                }
            });
        }
        return response;
    }
}
