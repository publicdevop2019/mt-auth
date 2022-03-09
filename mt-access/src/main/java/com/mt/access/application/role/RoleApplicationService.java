package com.mt.access.application.role;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.role.command.RoleCreateCommand;
import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.endpoint.event.EndpointShareRemoved;
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
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.access.domain.model.permission.Permission.*;

@Slf4j
@Service
public class RoleApplicationService {
    private static final String ROLE = "Role";

    public SumPagedRep<Role> getByQuery(String queryParam, String pageParam, String skipCount) {
        RoleQuery roleQuery = new RoleQuery(queryParam, pageParam, skipCount);
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), VIEW_ROLE_SUMMARY);
        return DomainRegistry.getRoleRepository().getByQuery(roleQuery);
    }

    public Optional<Role> getById(String projectId, String id) {
        RoleQuery roleQuery = new RoleQuery(new RoleId(id), new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), VIEW_ROLE);
        return DomainRegistry.getRoleRepository().getByQuery(roleQuery).findFirst();
    }

    public Optional<Role> internalGetById(RoleId id) {
        return DomainRegistry.getRoleRepository().getById(id);
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, RoleUpdateCommand command, String changeId) {
        RoleId roleId = new RoleId(id);
        RoleQuery roleQuery = new RoleQuery(roleId, new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), EDIT_ROLE);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Role> first = DomainRegistry.getRoleRepository().getByQuery(roleQuery).findFirst();
            first.ifPresent(e -> {
                e.replace(command.getName(), command.getDescription(),
                        command.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()),
                        command.getExternalPermissionIds() != null ? command.getExternalPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()) : null
                );
                DomainRegistry.getRoleRepository().add(e);
            });
            return null;
        }, ROLE);
    }

    @SubscribeForEvent
    @Transactional
    public void remove(String projectId, String id, String changeId) {
        RoleId roleId = new RoleId(id);
        RoleQuery roleQuery = new RoleQuery(roleId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(roleQuery.getProjectIds(), DELETE_ROLE);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Role> corsProfile = DomainRegistry.getRoleRepository().getById(roleId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getRoleRepository().remove(e);
            });
            return null;
        }, ROLE);
    }

    /**
     * create role, permissions must belong to root node
     *
     * @param command
     * @param changeId
     * @return
     */
    @SubscribeForEvent
    @Transactional
    public String create(RoleCreateCommand command, String changeId) {
        RoleId roleId = new RoleId();
        DomainRegistry.getPermissionCheckService().canAccess(new ProjectId(command.getProjectId()), CREATE_ROLE);
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Role role = Role.createRoleForTenant(
                    new ProjectId(command.getProjectId()),
                    roleId,
                    command.getName(),
                    command.getDescription(),
                    command.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()),
                    RoleType.USER,
                    command.getParentId() != null ? new RoleId(command.getParentId()) : null,
                    command.getExternalPermissionIds() != null ? command.getExternalPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()) : null
            );
            DomainRegistry.getRoleRepository().add(role);
            return roleId.getDomainId();
        }, ROLE);
    }

    /**
     * create admin role to mt-auth and default user role to target project
     *
     * @param deserialize
     */
    @SubscribeForEvent
    @Transactional
    public void handle(ProjectPermissionCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle project permission created event");
            ProjectId tenantProjectId = deserialize.getProjectId();
            ProjectId authPId = new ProjectId(AppConstant.MT_AUTH_PROJECT_ID);
            UserId creator = deserialize.getCreator();
            Set<PermissionId> permissionIdSet = deserialize.getDomainIds().stream().map(e -> new PermissionId(e.getDomainId())).collect(Collectors.toSet());
            Role.onboardNewProject(authPId, tenantProjectId, permissionIdSet, creator);
            return null;
        }, ROLE);
    }

    public SumPagedRep<Role> getByQuery(RoleQuery roleQuery) {
        return DomainRegistry.getRoleRepository().getByQuery(roleQuery);
    }

    /**
     * create placeholder role when new client created
     *
     * @param deserialize
     */
    @SubscribeForEvent
    @Transactional
    public void handle(ClientCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle client created event");
            ProjectId projectId = deserialize.getProjectId();
            ClientId clientId = new ClientId(deserialize.getDomainId().getDomainId());
            RoleId roleId = deserialize.getRoleId();
            Set<Role> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery((RoleQuery) e), new RoleQuery(projectId, new RoleId("null")));
            Optional<Role> first = allByQuery.stream().filter(e -> RoleType.CLIENT_ROOT.equals(e.getType())).findFirst();
            if (first.isEmpty()) {
                throw new IllegalStateException("unable to find root client role");
            }
            Role userRole = Role.autoCreate(projectId, roleId, clientId.getDomainId(), null, Collections.emptySet(), RoleType.CLIENT, first.get().getRoleId(), null);
            DomainRegistry.getRoleRepository().add(userRole);
            return null;
        }, ROLE);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(EndpointShareRemoved deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle endpoint shared removed event");
            PermissionId permissionId = deserialize.getPermissionId();
            RoleQuery roleQuery = new RoleQuery(permissionId);
            Set<Role> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery((RoleQuery) e), roleQuery);
            allByQuery.forEach(e -> {
                e.removeExternalPermission(permissionId);
                DomainRegistry.getRoleRepository().add(e);
            });
            return null;
        }, ROLE);
    }
}
