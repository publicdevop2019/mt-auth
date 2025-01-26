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
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.domain.model.validate.ValidationNotificationHandler;
import com.mt.common.domain.model.validate.Validator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RoleValidationService {

    public void validate(boolean newProjectOrClient, Role role,
                         ValidationNotificationHandler handler) {
        //skip validation for system create to boost performance
        if (Utility.isFalse(role.getSystemCreate())) {
            checkParentId(newProjectOrClient, role);
            permissionMustBeSameProject(role, handler);
            checkPermission(role.getApiPermissionIds(), PermissionType.API);
            checkPermission(role.getCommonPermissionIds(), PermissionType.COMMON);
        }
    }

    private void permissionMustBeSameProject(Role role, ValidationNotificationHandler handler) {
        Set<PermissionId> commonPermissionIds = role.getCommonPermissionIds();
        if (commonPermissionIds != null && !commonPermissionIds.isEmpty()) {
            Set<Permission> permissions = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(commonPermissionIds));
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
        Set<PermissionId> apiPermissionIds = role.getApiPermissionIds();
        if (apiPermissionIds != null && !apiPermissionIds.isEmpty()) {
            Set<Permission> permissions = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getPermissionRepository().query(e),
                    PermissionQuery.internalQuery(apiPermissionIds));
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
        Set<PermissionId> externalPermissionIds = role.getExternalPermissionIds();
        if (externalPermissionIds != null && !externalPermissionIds.isEmpty()) {
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
                if (!allowedPermission.containsAll(externalPermissionIds)) {
                    handler.handleError("external permissions not allowed");
                }
            } else {
                handler.handleError("external permissions not found");
            }
        }
    }

    private void checkParentId(boolean newProjectOrClient, Role role) {
        if (role.getParentId() != null && !newProjectOrClient) {
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
