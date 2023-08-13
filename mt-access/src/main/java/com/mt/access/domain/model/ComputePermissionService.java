package com.mt.access.domain.model;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ComputePermissionService {
    /**
     * compute user permission id set
     * common permission will be used to find linked api permission
     *
     * @param userRelation user relation to project
     * @return permission id collection
     */
    public Set<PermissionId> compute(UserRelation userRelation) {
        if (userRelation == null) {
            return Collections.emptySet();
        }
        Set<RoleId> standaloneRoles = userRelation.getStandaloneRoles();
        log.debug("role id found {}",
            CommonDomainRegistry.getCustomObjectSerializer().serialize(standaloneRoles));
        Set<Role> roles = QueryUtility.getAllByQuery(
            q -> ApplicationServiceRegistry.getRoleApplicationService().query(q),
            new RoleQuery(standaloneRoles));
        Set<PermissionId> commonPermissionIds =
            roles.stream().flatMap(e -> e.getCommonPermissionIds().stream())
                .collect(Collectors.toSet());
        log.debug("common permission id found {}",
            CommonDomainRegistry.getCustomObjectSerializer().serialize(commonPermissionIds));
        Set<PermissionId> totalPermissions =
            roles.stream().flatMap(e -> e.getTotalPermissionIds().stream()
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
