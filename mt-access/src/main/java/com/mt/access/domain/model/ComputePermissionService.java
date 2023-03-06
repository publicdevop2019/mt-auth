package com.mt.access.domain.model;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

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
        Set<RoleId> standaloneRoles = userRelation.getStandaloneRoles();
        Set<Role> allByQuery = QueryUtility.getAllByQuery(
            q -> ApplicationServiceRegistry.getRoleApplicationService().getByQuery(q),
            new RoleQuery(standaloneRoles));
        Set<PermissionId> commonPermissionIds =
            allByQuery.stream().flatMap(e -> e.getCommonPermissionIds().stream())
                .collect(Collectors.toSet());
        Set<PermissionId> collect =
            allByQuery.stream().flatMap(e -> e.getTotalPermissionIds().stream()
            ).collect(Collectors.toSet());
        if (!commonPermissionIds.isEmpty()) {
            Set<PermissionId> linkedApiPermissionFor =
                DomainRegistry.getPermissionRepository()
                    .getLinkedApiPermissionFor(commonPermissionIds);
            collect.addAll(linkedApiPermissionFor);
        }
        return collect;
    }
}
