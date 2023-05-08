package com.mt.access.domain.model.role;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public interface RoleRepository {
    void add(Role role);

    SumPagedRep<Role> query(RoleQuery roleQuery);

    void remove(Role e);

    Role byNullable(RoleId id);

    default Role by(RoleId id){
        Role byId = byNullable(id);
        Validator.notNull(byId);
        return byId;
    }

    Set<ProjectId> getProjectIds();

    long countProjectCreateTotal(ProjectId projectId);
}
