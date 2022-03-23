package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CORSProfileId;
import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface EndpointRepository {

    Optional<Endpoint> endpointOfId(EndpointId endpointId);

    void add(Endpoint endpoint);

    void remove(Endpoint endpoint);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery);

    void remove(Collection<Endpoint> endpoints);

    Set<CacheProfileId> getCacheProfileIds();

    Set<CORSProfileId> getCorsProfileIds();

    Set<ClientId> getClientIds();

}
