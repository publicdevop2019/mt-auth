package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface EndpointRepository {

    Optional<Endpoint> endpointOfId(EndpointId endpointId);

    void add(Endpoint endpoint);

    void remove(Endpoint endpoint);

    void remove(Collection<Endpoint> endpoints);

    SumPagedRep<Endpoint> endpointsOfQuery(EndpointQuery endpointQuery);

    Set<CacheProfileId> getCacheProfileIds();

    Set<CorsProfileId> getCorsProfileIds();

    Set<ClientId> getClientIds();

    long countTotal();

    long countSharedTotal();

    long countPublicTotal();

    long countProjectTotal(ProjectId projectId);
}
