package com.mt.access.domain.model.endpoint;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Collection;
import java.util.Optional;

public interface EndpointRepository {

    Optional<Endpoint> endpointOfId(EndpointId endpointId);

    void add(Endpoint endpoint);

    void remove(Endpoint endpoint);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery);

    void remove(Collection<Endpoint> endpoints);
}
