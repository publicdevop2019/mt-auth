package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Set;
import java.util.stream.Collectors;

public class UserRelationValidator {
    public static Set<Role> validateTenantRoleAssign(Set<String> roles, UserRelation userRelation) {
        Validator.notNull(roles);
        Validator.notEmpty(roles);
        Set<RoleId> roleIds =
            roles.stream().map(RoleId::new).collect(Collectors.toSet());
        Set<Role> roleSet = QueryUtility
            .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                new RoleQuery(roleIds));
        Set<ProjectId> projectIds =
            roleSet.stream().map(Role::getProjectId).collect(Collectors.toSet());
        if (projectIds.size() != 1 ||
            !projectIds.stream().findFirst().get().equals(userRelation.getProjectId())) {
            throw new DefinedRuntimeException("role project id should be same", "1087",
                HttpResponseCode.BAD_REQUEST);
        }
        return roleSet;
    }

    public static void validateAllAssignedRoles(Set<RoleId> roleIds) {
        if (Checker.notNull(roleIds)) {
            Set<Role> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                    new RoleQuery(roleIds));
            if (roleIds.size() != allByQuery.size()) {
                HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
                handler.handleError("not able to find all roles");
            }
        }
    }
}
