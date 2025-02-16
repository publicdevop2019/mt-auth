package com.mt.access.domain.model;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComputePermissionService {
    /**
     * compute user permission id set
     * common permission will be used to find linked api permission
     *
     * @param userRelation   user relation to project
     * @param defaultProject default project id
     * @return permission id collection
     */
    public Set<PermissionId> compute(UserRelation userRelation,
                                     @Nullable ProjectId defaultProject) {
        if (userRelation == null) {
            return Collections.emptySet();
        }
        Set<RoleId> standaloneRoles =
            DomainRegistry.getUserRelationRoleIdRepository().query(userRelation);
        log.debug("role id found {}",
            CommonDomainRegistry.getCustomObjectSerializer().serialize(standaloneRoles));
        Set<Role> nextRoles = QueryUtility.getAllByQuery(
            q -> ApplicationServiceRegistry.getRoleApplicationService().query(q),
            new RoleQuery(standaloneRoles));
        log.debug("roles retrieved");
        //only get root project user role and default tenant project admin role (if exist)
        if (defaultProject != null) {
            Optional<Role> optionalRole =
                nextRoles.stream().filter(e -> e.getTenantId().equals(defaultProject)).findFirst();

            nextRoles = nextRoles.stream()
                .filter(e -> e.getTenantId().equals(new ProjectId(AppConstant.MAIN_PROJECT_ID)))
                .collect(Collectors.toSet());

            optionalRole.ifPresent(nextRoles::add);
        }

        Set<PermissionId> commonPermissionIds =
            nextRoles.stream()
                .flatMap(e -> DomainRegistry.getCommonPermissionIdRepository().query(e).stream())
                .collect(Collectors.toSet());
        log.debug("common permission id found {}",
            CommonDomainRegistry.getCustomObjectSerializer().serialize(commonPermissionIds));
        Set<PermissionId> totalPermissions =
            nextRoles.stream().flatMap(e -> {
                    Set<PermissionId> totalPerm = new HashSet<>();
                    Set<PermissionId> comPerm =
                        DomainRegistry.getCommonPermissionIdRepository().query(e);
                    Set<PermissionId> apiPerm = DomainRegistry.getApiPermissionIdRepository().query(e);
                    Set<PermissionId> extPerm =
                        DomainRegistry.getExternalPermissionIdRepository().query(e);
                    totalPerm.addAll(comPerm);
                    totalPerm.addAll(apiPerm);
                    totalPerm.addAll(extPerm);
                    return totalPerm.stream();
                }
            ).collect(Collectors.toSet());
        log.debug("total permission id found {}",
            CommonDomainRegistry.getCustomObjectSerializer().serialize(totalPermissions));
        if (!commonPermissionIds.isEmpty()) {
            Set<PermissionId> linkedApiPermissionFor =
                DomainRegistry.getPermissionRepository()
                    .getLinkedApiPermissionFor(commonPermissionIds);
            totalPermissions.addAll(linkedApiPermissionFor);
        }
        return totalPermissions;
    }
}
