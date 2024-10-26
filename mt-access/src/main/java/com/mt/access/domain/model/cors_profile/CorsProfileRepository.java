package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface CorsProfileRepository {
    CorsProfile query(CorsProfileId id);

    default CorsProfile get(CorsProfileId id) {
        CorsProfile corsProfile = query(id);
        Validator.notNull(corsProfile);
        return corsProfile;
    }

    void add(CorsProfile corsProfile);

    void update(CorsProfile old, CorsProfile updated);

    void remove(CorsProfile corsProfile);

    SumPagedRep<CorsProfile> query(CorsProfileQuery endpointQuery);

}
