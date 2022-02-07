package com.mt.access.domain.model;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.restful.query.QueryUtility;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
@Service
public class ComputePermissionService {
    public Set<PermissionId> compute(UserRelation userRelation){
        Set<RoleId> standaloneRoles = userRelation.getStandaloneRoles();
        Set<Role> allByQuery = QueryUtility.getAllByQuery(q -> ApplicationServiceRegistry.getRoleApplicationService().query((RoleQuery) q), new RoleQuery(standaloneRoles));
        return allByQuery.stream().flatMap(e -> e.getPermissionIds().stream()).collect(Collectors.toSet());
    }
}
