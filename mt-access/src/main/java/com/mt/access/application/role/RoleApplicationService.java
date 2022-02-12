package com.mt.access.application.role;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.role.command.RoleCreateCommand;
import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.client.event.ClientCreated;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.RoleType;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleApplicationService {
    private static final String ROLE = "Role";

    public SumPagedRep<Role> query(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getRoleRepository().getByQuery(new RoleQuery(queryParam, pageParam, skipCount));
    }

    public Optional<Role> getById(String id) {
        return DomainRegistry.getRoleRepository().getById(new RoleId(id));
    }
    public Optional<Role> getById(RoleId id) {
        return DomainRegistry.getRoleRepository().getById(id);
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, RoleUpdateCommand command, String changeId) {
        RoleId RoleId = new RoleId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Role> first = DomainRegistry.getRoleRepository().getByQuery(new RoleQuery(RoleId)).findFirst();
            first.ifPresent(e -> {
                e.replace(command.getName(),command.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()));
                DomainRegistry.getRoleRepository().add(e);
            });
            return null;
        }, ROLE);
    }

    @SubscribeForEvent
    @Transactional
    public void remove(String id, String changeId) {
        RoleId RoleId = new RoleId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Role> corsProfile = DomainRegistry.getRoleRepository().getById(RoleId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getRoleRepository().remove(e);
            });
            return null;
        }, ROLE);
    }

    @SubscribeForEvent
    @Transactional
    public String create(RoleCreateCommand command, String changeId) {
        RoleId roleId = new RoleId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Set<PermissionId> collect = command.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet());
            Set<Permission> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery((PermissionQuery) e), new PermissionQuery(collect));
            Set<PermissionId> collect1 = allByQuery.stream().filter(e -> e.getLinkedApiPermissionId() != null).map(Permission::getLinkedApiPermissionId).collect(Collectors.toSet());
            collect.addAll(collect1);
            Role role = new Role(
                    new ProjectId(command.getProjectId()),
                    roleId,
                    command.getName(),
                    command.getDescription(),
                    collect,
                    RoleType.USER,
                    command.getParentId() != null ? new RoleId(command.getParentId()) : null,
                    null
            );
            DomainRegistry.getRoleRepository().add(role);
            return roleId.getDomainId();
        }, ROLE);
    }

    /**
     * create admin role to mt-auth and default user role to target project
     * @param deserialize
     */
    @SubscribeForEvent
    @Transactional
    public void handle(ProjectPermissionCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle project permission created event");
            ProjectId tenantProjectId = new ProjectId(deserialize.getProjectId().getDomainId());
            ProjectId authPId = new ProjectId(AppConstant.MT_AUTH_PROJECT_ID);
            Set<PermissionId> permissionIdSet = deserialize.getDomainIds().stream().map(e -> new PermissionId(e.getDomainId())).collect(Collectors.toSet());
            Set<PermissionId> defaultApiPermissions = Arrays.stream(AppConstant.MT_AUTH_TENANT_ADMIN_DEFAULT_PERMISSIONS.split(",")).map(PermissionId::new).collect(Collectors.toSet());
            permissionIdSet.addAll(defaultApiPermissions);
            RoleId roleId = new RoleId();
            RoleId roleId1 = new RoleId();
            Role rootRole = new Role(authPId,roleId , tenantProjectId.getDomainId(), "",permissionIdSet , RoleType.PROJECT, null,tenantProjectId);
            Role adminRole = new Role(authPId, new RoleId(), "PROJECT_ADMIN", "",permissionIdSet , RoleType.USER, roleId,tenantProjectId);

            Role userRole = new Role(tenantProjectId, new RoleId(), "PROJECT_USER", "", Collections.emptySet(), RoleType.USER, roleId1,null);
            Role tenantClientRoot = new Role(tenantProjectId, new RoleId(), "CLIENT_ROLE", "", Collections.emptySet(), RoleType.CLIENT_ROOT, null,null);
            Role tenantUserRoot = new Role(tenantProjectId, roleId1, tenantProjectId.getDomainId(), "", Collections.emptySet(), RoleType.PROJECT, null,null);

            DomainRegistry.getRoleRepository().add(adminRole);
            DomainRegistry.getRoleRepository().add(userRole);
            DomainRegistry.getRoleRepository().add(rootRole);
            DomainRegistry.getRoleRepository().add(tenantClientRoot);
            DomainRegistry.getRoleRepository().add(tenantUserRoot);
            DomainEventPublisher.instance().publish(new NewProjectRoleCreated(adminRole.getRoleId(),userRole.getRoleId(),deserialize.getProjectId(),permissionIdSet,deserialize.getCreator()));
            return null;
        }, ROLE);
    }

    public SumPagedRep<Role> query(RoleQuery roleQuery) {
        return DomainRegistry.getRoleRepository().getByQuery(roleQuery);
    }

    /**
     * create placeholder role when new client created
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
            Set<Role> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getRoleRepository().getByQuery((RoleQuery) e), new RoleQuery(projectId,new RoleId("null")));
            Optional<Role> first = allByQuery.stream().filter(e -> RoleType.CLIENT_ROOT.equals(e.getType())).findFirst();
            if(first.isEmpty()){
                throw new IllegalStateException("unable to find root client role");
            }
            Role userRole = new Role(projectId, roleId, clientId.getDomainId(), "SYSTEM_AUTO_CREATE", Collections.emptySet(), RoleType.CLIENT,first.get().getRoleId(), null);
            DomainRegistry.getRoleRepository().add(userRole);
            return null;
        }, ROLE);
    }
}
