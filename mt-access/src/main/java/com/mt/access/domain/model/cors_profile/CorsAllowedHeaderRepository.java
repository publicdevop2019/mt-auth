package com.mt.access.domain.model.cors_profile;

import java.util.Set;

public interface CorsAllowedHeaderRepository {
    Set<String> query(CorsProfile corsProfile);

    void remove(CorsProfile corsProfile, Set<String> headers);

    void add(CorsProfile corsProfile, Set<String> headers);

    void removeAll(CorsProfile corsProfile, Set<String> headers);
}
