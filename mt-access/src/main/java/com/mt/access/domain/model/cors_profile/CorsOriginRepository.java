package com.mt.access.domain.model.cors_profile;

import java.util.Set;

public interface CorsOriginRepository {
    Set<Origin> query(CorsProfile corsProfile);

    void remove(CorsProfile corsProfile, Set<Origin> origins);

    void add(CorsProfile corsProfile, Set<Origin> origins);

    void removeAll(CorsProfile corsProfile, Set<Origin> origins);
}
