package com.mt.access.domain.model.role;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.EndpointId;
import com.mt.access.domain.model.permission.Permission;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.permission.PermissionQuery;
import com.mt.access.domain.model.permission.PermissionType;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleValidationService {

    public void validate(Role role,
                         Set<PermissionId> comPerm,
                         Set<PermissionId> apiPerm,
                         Set<PermissionId> extPerm) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        checkParentId(role);
        permissionMustBeSameProject(role, comPerm, apiPerm, extPerm, handler);
        checkPermission(apiPerm, PermissionType.API);
        checkPermission(comPerm, PermissionType.COMMON);
    }

    public void validate(Role role,
                         Set<PermissionId> comPerm
    ) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        checkParentId(role);
        permissionMustBeSameProject(role, comPerm, Collections.emptySet(), Collections.emptySet(),
            handler);
        checkPermission(comPerm, PermissionType.COMMON);
    }

    public void validate(Role role,
                         Set<PermissionId> apiPerm,
                         Set<PermissionId> extPerm) {
        ValidationNotificationHandler handler = new HttpValidationNotificationHandler();
        checkParentId(role);
        permissionMustBeSameProject(role, Collections.emptySet(), apiPerm, extPerm, handler);
        checkPermission(apiPerm, PermissionType.API);
    }

    public void validate(Role role) {
        checkParentId(role);
    }

    private void permissionMustBeSameProject(
        Role role,
        Set<PermissionId> comPerm,
        Set<PermissionId> apiPerm,
        Set<PermissionId> extPerm,
        ValidationNotificationHandler handler
    ) {
        if (comPerm != null && !comPerm.isEmpty()) {
            Set<Permission> permissions = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(comPerm));
            Set<ProjectId> permProjectIds =
                permissions.stream().map(Permission::getProjectId).collect(Collectors.toSet());
            if (permProjectIds.size() != 1) {
                handler.handleError(
                    "common permissions added to role must belong to same tenant project");
            }
            ProjectId projectId = permProjectIds.stream().findFirst().get();
            if (!projectId.equals(role.getProjectId())) {
                log.debug("permission project id is {} and role project id is {}", projectId,
                    role.getProjectId());
                handler.handleError(
                    "common permissions and role must belong to same tenant project");
            }
        }
        if (apiPerm != null && !apiPerm.isEmpty()) {
            Set<Permission> permissions = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(apiPerm));
            Set<ProjectId> collect =
                permissions.stream().map(Permission::getProjectId).collect(Collectors.toSet());
            if (collect.size() != 1) {
                handler.handleError(
                    "api permissions added to role must belong to same tenant project");
            }
            if (!collect.stream().findFirst().get().equals(role.getProjectId())) {
                handler.handleError("api permissions and role must belong to same tenant project");
            }
        }
        if (extPerm != null && !extPerm.isEmpty()) {
            //get subscribed endpoints
            Set<EndpointId> endpointIds =
                ApplicationServiceRegistry.getSubRequestApplicationService()
                    .internalSubscribedEndpointIds(role.getProjectId());
            if (!endpointIds.isEmpty()) {
                Set<Endpoint> endpoints =
                    ApplicationServiceRegistry.getEndpointApplicationService()
                        .internalQuery(endpointIds);
                //get endpoint's permission
                Set<PermissionId> allowedPermission =
                    endpoints.stream().map(Endpoint::getPermissionId)
                        .filter(
                            Objects::nonNull)//filter shared endpoint that has no permission check
                        .collect(Collectors.toSet());
                if (!allowedPermission.containsAll(extPerm)) {
                    handler.handleError("external permissions not allowed");
                }
            } else {
                handler.handleError("external permissions not found");
            }
        }
    }

    private void checkParentId(Role role) {
        if (role.getParentId() != null) {
            Role parentRole = DomainRegistry.getRoleRepository().get(role.getParentId());
            log.debug("comparing parent role {}, project id is {} and role {}, project id is {}",
                parentRole.getRoleId(), parentRole.getProjectId(), role.getRoleId(),
                role.getProjectId());
            //must be same project
            Validator.equals(parentRole.getProjectId(), role.getProjectId());
            //must be user type
            Validator.equals(parentRole.getType(), RoleType.USER);
        }
    }

    private void checkPermission(Set<PermissionId> permissionIds, PermissionType type) {
        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<Permission> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(permissionIds, type));
            if (log.isDebugEnabled() && allByQuery.size() != permissionIds.size()) {
                Set<PermissionId> collect =
                    allByQuery.stream().map(Permission::getPermissionId)
                        .collect(Collectors.toSet());
                Set<PermissionId> collect1 =
                    permissionIds.stream().filter(e -> !collect.contains(e))
                        .collect(Collectors.toSet());
                log.debug("unable find permission id(s) {}", collect1);
            }
            Validator
                .sizeEquals(allByQuery, permissionIds);
        }
    }
}
