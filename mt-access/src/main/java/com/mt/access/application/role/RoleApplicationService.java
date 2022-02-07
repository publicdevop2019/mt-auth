package com.mt.access.application.role;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.role.command.RoleCreateCommand;
import com.mt.access.application.role.command.RoleUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.event.ProjectPermissionCreated;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.role.event.NewProjectRoleCreated;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.model.domain_event.DomainEventPublisher;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class RoleApplicationService {
    @Value("${mt.project.id}")
    private String authProjectId;
    private static final String ROLE = "Role";

    public SumPagedRep<Role> query(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getRoleRepository().getByQuery(new RoleQuery(queryParam, pageParam, skipCount));
    }

    public Optional<Role> getById(String id) {
        return DomainRegistry.getRoleRepository().getById(new RoleId(id));
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
            Role role = new Role(
                    new ProjectId(command.getProjectId()),
                    roleId,
                    command.getName(),
                    command.getDescription(),
                    command.getPermissionIds().stream().map(PermissionId::new).collect(Collectors.toSet()),
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
            ProjectId authPId = new ProjectId(authProjectId);
            Set<PermissionId> permissionIdSet = deserialize.getDomainIds().stream().map(e -> new PermissionId(e.getDomainId())).collect(Collectors.toSet());
            Role adminRole = new Role(authPId, new RoleId(), "PROJECT_ADMIN", "",permissionIdSet , null,tenantProjectId);
            Role userRole = new Role(tenantProjectId, new RoleId(), "PROJECT_USER", "", Collections.emptySet(), null,null);
            DomainRegistry.getRoleRepository().add(adminRole);
            DomainRegistry.getRoleRepository().add(userRole);
            DomainEventPublisher.instance().publish(new NewProjectRoleCreated(adminRole.getRoleId(),userRole.getRoleId(),deserialize.getProjectId(),permissionIdSet,deserialize.getCreator()));
            return null;
        }, ROLE);
    }

    public SumPagedRep<Role> query(RoleQuery roleQuery) {
        return DomainRegistry.getRoleRepository().getByQuery(roleQuery);
    }
}
