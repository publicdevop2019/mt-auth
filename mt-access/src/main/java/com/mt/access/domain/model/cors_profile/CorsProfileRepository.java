package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface CorsProfileRepository {
    CorsProfile byNullable(CorsProfileId id);
    default CorsProfile by(CorsProfileId id){
        CorsProfile corsProfile = byNullable(id);
        Validator.notNull(corsProfile);
        return corsProfile;
    }

    void add(CorsProfile corsProfile);

    void remove(CorsProfile corsProfile);

    SumPagedRep<CorsProfile> query(CorsProfileQuery endpointQuery);

}
