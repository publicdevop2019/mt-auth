package com.mt.access.domain.model.user;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.role.RoleId;
import com.mt.common.domain.model.validate.Validator;
import java.util.Collections;
import java.util.Set;

public class UserRelationRoleId {
    public static void internalAdd(UserRelation userRelation, RoleId roleId) {
        Set<RoleId> roleIds = Collections.singleton(roleId);
        Validator.notNull(roleId);
        DomainRegistry.getUserRelationRoleIdRepository()
            .add(userRelation, roleIds);
    }

    public static void add(UserRelation userRelation, Set<RoleId> roleIds) {
        Set<RoleId> extRoleIds =
            DomainRegistry.getUserRelationRoleIdRepository().query(userRelation);
        if (!extRoleIds.containsAll(roleIds)) {
            UserRelationValidator.validateAddedRoles(roleIds, userRelation);
            DomainRegistry.getUserRelationRoleIdRepository().add(userRelation, roleIds);
        }
    }

    public static void remove(UserRelation relation, Set<RoleId> old, RoleId rmRoleId) {
        if (old.contains(rmRoleId)) {
            UserRelationValidator.validateRemovedRole(rmRoleId);
            DomainRegistry.getUserRelationRoleIdRepository().remove(relation, rmRoleId);
        }
    }
}
