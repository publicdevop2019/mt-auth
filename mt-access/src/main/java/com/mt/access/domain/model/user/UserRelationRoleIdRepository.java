package com.mt.access.domain.model.user;

import com.mt.access.domain.model.role.RoleId;
import java.util.Set;

public interface UserRelationRoleIdRepository {
    Set<RoleId> query(UserRelation userRelation);

    void add(UserRelation userRelation, Set<RoleId> roleIds);

    void remove(UserRelation userRelation, RoleId roleId);
}
