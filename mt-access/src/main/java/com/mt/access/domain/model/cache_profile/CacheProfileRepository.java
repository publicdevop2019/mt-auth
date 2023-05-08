package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface CacheProfileRepository {
    default CacheProfile by(CacheProfileId id) {
        CacheProfile cacheProfile = byNullable(id);
        Validator.notNull(cacheProfile);
        return cacheProfile;
    }

    CacheProfile byNullable(CacheProfileId id);

    void add(CacheProfile cacheProfile);

    void remove(CacheProfile cacheProfile);

    SumPagedRep<CacheProfile> query(CacheProfileQuery cacheProfileQuery);
}
