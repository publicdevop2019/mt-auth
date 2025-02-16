package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class UserRelationRoleId {
    public static void internalAdd(UserRelation userRelation, RoleId roleId) {
        Set<RoleId> roleIds = Collections.singleton(roleId);
        Validator.notNull(roleId);
        DomainRegistry.getUserRelationRoleIdRepository()
            .add(userRelation, roleIds);
    }

    public static void add(UserRelation userRelation, Set<RoleId> roleIds) {
        //remove default user so mt-auth will not be miss added to tenant list
        Set<RoleId> newRoleIds =
            roleIds.stream().filter(e -> !new RoleId(AppConstant.MAIN_USER_ROLE_ID).equals(e))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<RoleId> extRoleIds =
            DomainRegistry.getUserRelationRoleIdRepository().query(userRelation);
        if (!extRoleIds.containsAll(roleIds)) {
            UserRelationValidator.validateAllAssignedRoles(roleIds, userRelation);
            DomainRegistry.getUserRelationRoleIdRepository().add(userRelation, newRoleIds);
        }
    }

    public static void remove(UserRelation relation, Set<RoleId> old, RoleId roleId) {
        if (old.contains(roleId)) {
            DomainRegistry.getUserRelationRoleIdRepository().remove(relation, roleId);
        }
    }
}
