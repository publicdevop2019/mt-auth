package com.mt.access.domain.model.cache_profile;

import com.mt.common.domain.model.restful.SumPagedRep;
import com.mt.common.domain.model.validate.Validator;

public interface CacheProfileRepository {
    default CacheProfile get(CacheProfileId id) {
        CacheProfile cacheProfile = query(id);
        Validator.notNull(cacheProfile);
        return cacheProfile;
    }

    CacheProfile query(CacheProfileId id);

    void add(CacheProfile cacheProfile);

    void remove(CacheProfile cacheProfile);

    SumPagedRep<CacheProfile> query(CacheProfileQuery cacheProfileQuery);
}
