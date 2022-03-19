package com.mt.access.application.permission;

import com.github.fge.jsonpatch.JsonPatch;
import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.permission.command.PermissionCreateCommand;
import com.mt.access.application.permission.command.PermissionPatchCommand;
import com.mt.access.application.permission.command.PermissionUpdateCommand;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.endpoint.EndpointQuery;
import com.mt.access.domain.model.endpoint.event.EndpointShareAdded;
import com.mt.access.domain.model.endpoint.event.EndpointShareRemoved;
import com.mt.access.domain.model.endpoint.event.SecureEndpointCreated;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.project.event.ProjectCreated;
import com.mt.access.infrastructure.AppConstant;
import com.mt.access.infrastructure.JwtCurrentUserService;
import com.mt.common.application.CommonApplicationServiceRegistry;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.domain_event.SubscribeForEvent;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mt.access.domain.model.permission.Permission.*;

@Slf4j
@Service
public class PermissionApplicationService {
    private static final String PERMISSION = "Permission";

    public SumPagedRep<Permission> query(String queryParam, String pageParam, String skipCount) {
        PermissionQuery permissionQuery = new PermissionQuery(queryParam, pageParam, skipCount);
        DomainRegistry.getPermissionCheckService().canAccess(permissionQuery.getProjectIds(), VIEW_PERMISSION);
        return DomainRegistry.getPermissionRepository().getByQuery(permissionQuery);
    }

    public Optional<Permission> getById(String projectId, String id) {
        PermissionQuery permissionQuery = new PermissionQuery(new PermissionId(id), new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(permissionQuery.getProjectIds(), VIEW_PERMISSION);
        return DomainRegistry.getPermissionRepository().getByQuery(permissionQuery).findFirst();
    }

    @SubscribeForEvent
    @Transactional
    public void replace(String id, PermissionUpdateCommand command, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery = new PermissionQuery(permissionId, new ProjectId(command.getProjectId()));
        DomainRegistry.getPermissionCheckService().canAccess(permissionQuery.getProjectIds(), EDIT_PERMISSION);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Optional<Permission> first = DomainRegistry.getPermissionRepository().getByQuery(permissionQuery).findFirst();
            first.ifPresent(ee -> {
                Set<PermissionId> linkedPermId = null;
                if (command.getLinkedApiIds() != null && !command.getLinkedApiIds().isEmpty()) {
                    Set<EndpointId> collect = command.getLinkedApiIds().stream().map(EndpointId::new).collect(Collectors.toSet());
                    Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e), new EndpointQuery(collect));
                    Validator.equalTo(allByQuery.size(), collect.size(), "unable to find all endpoint");
                    linkedPermId = allByQuery.stream().map(Endpoint::getPermissionId).collect(Collectors.toSet());
                }
                ee.replace(command.getName(),linkedPermId);
                DomainRegistry.getPermissionRepository().add(ee);
            });
            return null;
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public void remove(String projectId, String id, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery = new PermissionQuery(permissionId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(permissionQuery.getProjectIds(), EDIT_PERMISSION);
        CommonApplicationServiceRegistry.getIdempotentService().idempotent(changeId, (ignored) -> {
            Optional<Permission> permission = DomainRegistry.getPermissionRepository().getByQuery(permissionQuery).findFirst();
            permission.ifPresent(Permission::remove);
            return null;
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public void patch(String projectId, String id, JsonPatch command, String changeId) {
        PermissionId permissionId = new PermissionId(id);
        PermissionQuery permissionQuery = new PermissionQuery(permissionId, new ProjectId(projectId));
        DomainRegistry.getPermissionCheckService().canAccess(permissionQuery.getProjectIds(), EDIT_PERMISSION);
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (ignored) -> {
            Optional<Permission> corsProfile = DomainRegistry.getPermissionRepository().getByQuery(permissionQuery).findFirst();
            if (corsProfile.isPresent()) {
                Permission corsProfile1 = corsProfile.get();
                PermissionPatchCommand beforePatch = new PermissionPatchCommand(corsProfile1);
                PermissionPatchCommand afterPatch = CommonDomainRegistry.getCustomObjectSerializer().applyJsonPatch(command, beforePatch, PermissionPatchCommand.class);
                corsProfile1.patch(
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
        DomainRegistry.getPermissionCheckService().canAccess(new ProjectId(command.getProjectId()), CREATE_PERMISSION);
        return ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(changeId, (change) -> {
            Set<PermissionId> linkedPermId = null;
            if (command.getLinkedApiIds() != null && !command.getLinkedApiIds().isEmpty()) {
                Set<EndpointId> collect = command.getLinkedApiIds().stream().map(EndpointId::new).collect(Collectors.toSet());
                Set<Endpoint> allByQuery = QueryUtility.getAllByQuery(e -> DomainRegistry.getEndpointRepository().endpointsOfQuery(e), new EndpointQuery(collect));
                Validator.equalTo(allByQuery.size(), collect.size(), "unable to find all endpoint");
                linkedPermId = allByQuery.stream().map(Endpoint::getPermissionId).collect(Collectors.toSet());
            }
            Permission permission;
            if (command.getParentId() != null && !command.getParentId().isBlank()) {
                permission = Permission.manualCreate(new ProjectId(command.getProjectId()), permissionId, command.getName(), PermissionType.COMMON, new PermissionId(command.getParentId()), null, linkedPermId);
            } else {
                permission = Permission.manualCreate(new ProjectId(command.getProjectId()), permissionId, command.getName(), PermissionType.COMMON, null, null, linkedPermId);
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
            ProjectId projectId = new ProjectId(AppConstant.MT_AUTH_PROJECT_ID);
            Permission.onboardNewProject(projectId, tenantProjectId, deserialize.getCreator());
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
            Permission.addNewEndpoint(projectId, endpointId, permissionId, deserialize.isShared());
            return null;
        }, PERMISSION);
    }

    public SumPagedRep<Permission> sharedPermissions(String queryParam, String pageParam) {
        PermissionQuery permissionQuery = PermissionQuery.sharedQuery(queryParam, pageParam);
        return DomainRegistry.getPermissionRepository().getByQuery(permissionQuery);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(EndpointShareAdded deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle endpoint shared added event");
            PermissionId permissionId = deserialize.getPermissionId();
            Optional<Permission> byId = DomainRegistry.getPermissionRepository().getById(permissionId);
            byId.ifPresent(e -> e.setShared(true));
            return null;
        }, PERMISSION);
    }

    @SubscribeForEvent
    @Transactional
    public void handle(EndpointShareRemoved deserialize) {
        ApplicationServiceRegistry.getApplicationServiceIdempotentWrapper().idempotent(deserialize.getId().toString(), (ignored) -> {
            log.debug("handle endpoint shared remove event");
            PermissionId permissionId = deserialize.getPermissionId();
            Optional<Permission> byId = DomainRegistry.getPermissionRepository().getById(permissionId);
            byId.ifPresent(e -> e.setShared(false));
            return null;
        }, PERMISSION);
    }

    public Set<Permission> ui() {
        Set<ProjectId> tenantIds = DomainRegistry.getCurrentUserService().getTenantIds();
        return QueryUtility.getAllByQuery(e -> DomainRegistry.getPermissionRepository().getByQuery(e), PermissionQuery.uiPermissionQuery(tenantIds, reservedName));
    }
}
