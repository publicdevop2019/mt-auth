package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.restful.SumPagedRep;
import java.util.Optional;

public interface CorsProfileRepository {
    Optional<CorsProfile> corsProfileOfId(CorsProfileId id);

    void add(CorsProfile corsProfile);

    void remove(CorsProfile corsProfile);

    SumPagedRep<CorsProfile> corsProfileOfQuery(CorsProfileQuery endpointQuery);

}
