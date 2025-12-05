package com.mt.access.domain.model.endpoint;

import com.mt.access.domain.model.cache_profile.CacheProfileId;
import com.mt.access.domain.model.cors_profile.CorsProfileId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;
import java.util.Set;

public interface EndpointRepository {

    default Endpoint get(EndpointId endpointId) {
        Endpoint endpoint = query(endpointId);
        Validator.notNull(endpoint);
        return endpoint;
    }

    default Endpoint get(ProjectId projectId, EndpointId endpointId) {
        EndpointQuery endpointQuery =
            new EndpointQuery(endpointId, projectId);
        Endpoint endpoint = query(endpointQuery).findFirst().orElse(null);
        Validator.notNull(endpoint);
        return endpoint;
    }

    Endpoint query(EndpointId endpointId);

    void update(Endpoint old, Endpoint update);

    void add(Endpoint endpoint);

    void remove(Endpoint endpoint);

    void remove(Set<Endpoint> endpoints);

    SumPagedRep<Endpoint> query(EndpointQuery endpointQuery);

    Set<CacheProfileId> getCacheProfileIds();

    Set<CorsProfileId> getCorsProfileIds();

    Set<RouterId> getRouterIds();

    long countTotal();

    long countSharedTotal();

    long countPublicTotal();

    long countProjectTotal(ProjectId projectId);

    boolean checkDuplicate(RouterId routerId, String path, String method);
}
