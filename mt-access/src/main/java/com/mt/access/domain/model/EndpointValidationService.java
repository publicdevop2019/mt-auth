package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.cache_profile.CacheProfile;
import com.mt.access.domain.model.cors_profile.CorsProfile;
import com.mt.access.domain.model.endpoint.Endpoint;
import com.mt.access.domain.model.endpoint.Router;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import org.springframework.stereotype.Service;

@Service
public class EndpointValidationService {

    private static void compareProjectId(ProjectId a, ProjectId b) {
        if (!Checker.equals(a, b)) {
            throw new DefinedRuntimeException("project id mismatch", "1010",
                HttpResponseCode.BAD_REQUEST);
        }
    }

    public void validate(Endpoint endpoint) {
        hasValidRouterId(endpoint);
        hasValidCacheProfileId(endpoint);
        hasValidCorsProfileId(endpoint);
    }

    private void hasValidCorsProfileId(Endpoint endpoint) {
        if (endpoint.getCorsProfileId() != null) {
            CorsProfile corsProfile = DomainRegistry.getCorsProfileRepository()
                .get(endpoint.getCorsProfileId());
            compareProjectId(endpoint.getProjectId(), corsProfile.getProjectId());
        }
    }

    private void hasValidRouterId(Endpoint endpoint) {
        if (endpoint.getRouterId() != null) {
            Router router = DomainRegistry.getRouterRepository()
                .get(endpoint.getRouterId());
            compareProjectId(endpoint.getProjectId(), router.getProjectId());
        }
    }

    private void hasValidCacheProfileId(Endpoint endpoint) {
        if (endpoint.getCacheProfileId() != null) {
            CacheProfile cacheProfile = DomainRegistry.getCacheProfileRepository()
                .get(endpoint.getCacheProfileId());
            compareProjectId(endpoint.getProjectId(), cacheProfile.getProjectId());
        }
    }
}
