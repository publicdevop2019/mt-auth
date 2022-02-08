package com.mt.access.application.permission;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.command.PermissionPatchCommand;
import com.mt.access.application.permission.command.PermissionUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PermissionApplicationService {
    @Value("${mt.project.id}")
    private String authProjectId;
    private static final String PERMISSION = "Permission";

    public SumPagedRep<Permission> query(String queryParam, String pageParam, String skipCount) {
        return DomainRegistry.getPermissionRepository().getByQuery(new PermissionQuery(queryParam, pageParam, skipCount));
    }

    public Set<PermissionId> getPermissionsForTenantProject(ProjectId tenantId) {
        return QueryUtility.getAllByQuery((query) -> DomainRegistry.getPermissionRepository().getByQuery((PermissionQuery) query), PermissionQuery.tenantQuery(tenantId)).stream().map(Permission::getPermissionId).collect(Collectors.toSet());
    }

    public Optional<Permission> getById(String id) {
        return DomainRegistry.getPermissionRepository().getById(new PermissionId(id));
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, PermissionUpdateCommand command, String changeId) {
        PermissionId PermissionId = new PermissionId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Permission> first = DomainRegistry.getPermissionRepository().getByQuery(new PermissionQuery(PermissionId)).findFirst();
            first.ifPresent(e -> {
                e.replace(command.getName());
                DomainRegistry.getPermissionRepository().add(e);
            });
            return null;
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public void remove(String id, String changeId) {
        PermissionId PermissionId = new PermissionId(id);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Permission> corsProfile = DomainRegistry.getPermissionRepository().getById(PermissionId);
            corsProfile.ifPresent(e -> {
                DomainRegistry.getPermissionRepository().remove(e);
            });
            return null;
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String id, JsonPatch command, String changeId) {
        PermissionId PermissionId = new PermissionId(id);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Permission> corsProfile = DomainRegistry.getPermissionRepository().getById(PermissionId);
            if (corsProfile.isPresent()) {
                Permission corsProfile1 = corsProfile.get();
                PermissionPatchCommand beforePatch = new PermissionPatchCommand(corsProfile1);
                PermissionPatchCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PermissionPatchCommand.class);
                corsProfile1.replace(
                        afterPatch.getName()
                );
            }
            return null;
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public String create(PermissionCreateCommand command, String changeId) {
        PermissionId permissionId = new PermissionId();
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Permission permission;
            if (command.getParentId() != null) {
                permission = new Permission(new ProjectId(command.getProjectId()), permissionId, command.getName(), PermissionType.COMMON, new PermissionId(command.getParentId()),null);
            } else {
                permission = new Permission(new ProjectId(command.getProjectId()), permissionId, command.getName(), PermissionType.COMMON,null);
            }
            DomainRegistry.getPermissionRepository().add(permission);
            return permissionId.getDomainId();
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(ProjectCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle project created event");
            ProjectId tenantProjectId = new ProjectId(deserialize.getDomainId().getDomainId());
            ProjectId projectId = new ProjectId(authProjectId);
            Permission.onboardNewProject(projectId,tenantProjectId, deserialize.getCreator());
            return null;
        }, PERMISSION);
    }
    @SubscribeForEvent
    @Transactional
    public void handle(SecureEndpointCreated deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle endpoint created event");
            EndpointId endpointId = new EndpointId(deserialize.getDomainId().getDomainId());
            PermissionId permissionId = deserialize.getPermissionId();
            ProjectId projectId = deserialize.getProjectId();
            Permission.addNewEndpoint(projectId,endpointId,permissionId);
            return null;
        }, PERMISSION);
    }
}
