package com.mt.access.domain.model.organization;

import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;

public interface OrganizationRepository {
    void add(Organization role);

    SumPagedRep<Organization> getByQuery(OrganizationQuery roleQuery);

    void remove(Organization e);

    Optional<Organization> getById(OrganizationId id);
}
