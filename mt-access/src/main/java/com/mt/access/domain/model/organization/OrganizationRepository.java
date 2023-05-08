package com.mt.access.domain.model.organization;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface OrganizationRepository {
    void add(Organization role);

    SumPagedRep<Organization> query(OrganizationQuery roleQuery);

    void remove(Organization e);

    default Organization by(OrganizationId id) {
        Organization byIdNullable = byNullable(id);
        Validator.notNull(byIdNullable);
        return byIdNullable;
    }

    Organization byNullable(OrganizationId id);
}
