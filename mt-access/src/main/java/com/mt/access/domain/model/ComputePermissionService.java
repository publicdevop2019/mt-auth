package com.mt.access.domain.model;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.restful.query.QueryUtility;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

@Service
public class ComputePermissionService {
    public Set<PermissionId> compute(UserRelation userRelation) {
        Set<RoleId> standaloneRoles = userRelation.getStandaloneRoles();
        Set<Role> allByQuery = QueryUtility.getAllByQuery(
            q -> ApplicationServiceRegistry.getRoleApplicationService().getByQuery(q),
            new RoleQuery(standaloneRoles));
        return allByQuery.stream().flatMap(e -> {
                Stream<PermissionId> concat = Stream.empty();
                if (e.getPermissionIds() != null) {
                    concat = Stream.concat(concat, e.getPermissionIds().stream());
                }
                if (e.getExternalPermissionIds() != null) {
                    concat = Stream.concat(concat, e.getExternalPermissionIds().stream());
                }
                return concat;
            }
        ).collect(Collectors.toSet());
    }
}
