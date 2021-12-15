package com.mt.access.domain.model.cors_profile;

import com.mt.common.domain.model.restful.SumPagedRep;

import java.util.Optional;

public interface CORSProfileRepository {
    Optional<CORSProfile> corsProfileOfId(CORSProfileId id);

    void add(CORSProfile corsProfile);

    void remove(CORSProfile corsProfile);

    SumPagedRep<CORSProfile> corsProfileOfQuery(CORSProfileQuery endpointQuery);

}
