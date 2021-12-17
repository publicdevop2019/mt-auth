package com.mt.access.domain.model.system_role;

import com.mt.common.domain.model.restful.SumPagedRep;

public interface SystemRoleRepository {
    SumPagedRep<SystemRole> systemRoleOfQuery(SystemRoleQuery var0);

    void add(SystemRole var0);

    void remove(SystemRole e);
}
