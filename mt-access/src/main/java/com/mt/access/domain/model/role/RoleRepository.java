package com.mt.access.domain.model.role;

import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository {
    void add(Role role);

    void addAll(Set<Role> role);

    SumPagedRep<Role> query(RoleQuery roleQuery);

    void remove(Role e);

    Role query(RoleId id);

    default Role get(RoleId id){
        Role byId = query(id);
        Validator.notNull(byId);
        return byId;
    }
    default Role get(ProjectId projectId, RoleId id){
        Role role = query(new RoleQuery(id, projectId)).findFirst().orElse(null);
        Validator.notNull(role);
        return role;
    }

    Set<ProjectId> getProjectIds();

    long countProjectCreateTotal(ProjectId projectId);

    Optional<Role> queryClientRoot(ProjectId projectId);

    void update(Role old, Role updated);

    void removeReferredPermissionId(PermissionId permissionId);
}
