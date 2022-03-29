package com.mt.access.domain.model.role;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository {
    void add(Role role);

    SumPagedRep<Role> getByQuery(RoleQuery roleQuery);

    void remove(Role e);

    Optional<Role> getById(RoleId id);

    Set<ProjectId> getProjectIds();

}
