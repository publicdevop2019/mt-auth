package com.mt.access.domain.model.organization;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface OrganizationRepository {
    void add(Organization role);

    SumPagedRep<Organization> query(OrganizationQuery roleQuery);

    void remove(Organization e);

    default Organization get(OrganizationId id) {
        Organization organization = query(id);
        Validator.notNull(organization);
        return organization;
    }

    Organization query(OrganizationId id);
}
