package com.mt.access.domain.model.role;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface RoleRepository {
    void add(Role role);

    SumPagedRep<Role> getByQuery(RoleQuery roleQuery);

    void remove(Role e);

    Optional<Role> getById(RoleId id);
}
