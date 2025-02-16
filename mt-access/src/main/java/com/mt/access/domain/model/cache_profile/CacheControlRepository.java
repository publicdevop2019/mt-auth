package com.mt.access.domain.model.cache_profile;

import java.util.Set;

public interface CacheControlRepository {
    Set<CacheControlValue> query(CacheProfile cacheProfile);

    void remove(CacheProfile cacheProfile, Set<CacheControlValue> values);

    void add(CacheProfile cacheProfile, Set<CacheControlValue> values);

    void removeAll(CacheProfile cacheProfile, Set<CacheControlValue> values);
}
