package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.role.RoleQuery;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.restful.query.QueryUtility;
import com.mt.common.domain.model.validate.Utility;
import com.mt.common.infrastructure.HttpValidationNotificationHandler;
import java.util.Set;
import java.util.stream.Collectors;

public class UserRelationValidator {
    public static void validateAllAssignedRoles(Set<RoleId> roleIds, UserRelation userRelation) {
        if (Utility.notNull(roleIds)) {
            Set<Role> allByQuery = QueryUtility
                .getAllByQuery(e -> DomainRegistry.getRoleRepository().query(e),
                    new RoleQuery(roleIds));
            if (roleIds.size() != allByQuery.size()) {
                HttpValidationNotificationHandler handler = new HttpValidationNotificationHandler();
                handler.handleError("not able to find all roles");
            }
            Set<ProjectId> projectIds =
                allByQuery.stream().map(Role::getProjectId).collect(Collectors.toSet());
            if (projectIds.size() != 1) {
                throw new DefinedRuntimeException("role project id should be same", "1087",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (!projectIds.stream().findFirst().get().equals(userRelation.getProjectId())) {
                throw new DefinedRuntimeException("invalid role project id", "1094",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
    }
}
