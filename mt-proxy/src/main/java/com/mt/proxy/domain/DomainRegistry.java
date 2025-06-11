package com.mt.proxy.domain;

import com.mt.proxy.domain.rate_limit.RateLimitService;
import com.mt.proxy.infrastructure.CheckSumService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    @Getter
    private static RevokeTokenRepository revokeTokenRepository;
    @Getter
    private static RevokeTokenService revokeTokenService;
    @Getter
    private static RetrieveEndpointService retrieveEndpointService;
    @Getter
    private static RetrieveRegisterApplicationService retrieveRegisterApplicationService;
    @Getter
    private static JwtService jwtService;
    @Getter
    private static CheckSumService checkSumService;
    @Getter
    private static ProxyCacheService proxyCacheService;
    @Getter
    private static RetrieveJwtPublicKeyService retrieveJwtPublicKeyService;
    @Getter
    private static EndpointService endpointService;
    @Getter
    private static CsrfService csrfService;
    @Getter
    private static CorsService corsService;
    @Getter
    private static CacheService cacheService;
    @Getter
    private static RegisteredApplicationService registeredApplicationService;
    @Getter
    private static RateLimitService rateLimitService;
    @Getter
    private static JsonSanitizeService jsonSanitizeService;

    @Autowired
    public void setJsonSanitizeService(JsonSanitizeService jsonSanitizeService) {
        DomainRegistry.jsonSanitizeService = jsonSanitizeService;
    }

    @Autowired
    public void setRetrieveRegisterApplicationService(
        RetrieveRegisterApplicationService retrieveRegisterApplicationService) {
        DomainRegistry.retrieveRegisterApplicationService = retrieveRegisterApplicationService;
    }

    @Autowired
    public void setProxyCacheService(ProxyCacheService proxyCacheService) {
        DomainRegistry.proxyCacheService = proxyCacheService;
    }

    @Autowired
    public void setRetrieveJwtPublicKeyService(
        RetrieveJwtPublicKeyService retrieveJwtPublicKeyService) {
        DomainRegistry.retrieveJwtPublicKeyService = retrieveJwtPublicKeyService;
    }

    @Autowired
    public void setRevokeTokenRepository(RevokeTokenRepository revokeTokenRepository) {
        DomainRegistry.revokeTokenRepository = revokeTokenRepository;
    }

    @Autowired
    public void setCheckSumService(CheckSumService checkSumService) {
        DomainRegistry.checkSumService = checkSumService;
    }

    @Autowired
    public void setRetrieveEndpointService(RetrieveEndpointService retrieveEndpointService) {
        DomainRegistry.retrieveEndpointService = retrieveEndpointService;
    }

    @Autowired
    public void setRevokeTokenService(RevokeTokenService revokeTokenService) {
        DomainRegistry.revokeTokenService = revokeTokenService;
    }

    @Autowired
    public void setJwtService(JwtService jwtService) {
        DomainRegistry.jwtService = jwtService;
    }


    @Autowired
    public void setLoadEndpointService(EndpointService roadEndpointService) {
        DomainRegistry.endpointService = roadEndpointService;
    }

    @Autowired
    public void setCsrfService(CsrfService csrfService) {
        DomainRegistry.csrfService = csrfService;
    }

    @Autowired
    public void setCorsService(CorsService corsService) {
        DomainRegistry.corsService = corsService;
    }

    @Autowired
    public void setCacheService(CacheService cacheService) {
        DomainRegistry.cacheService = cacheService;
    }

    @Autowired
    public void setRegisteredApplicationService(
        RegisteredApplicationService registeredApplicationService) {
        DomainRegistry.registeredApplicationService = registeredApplicationService;
    }


    @Autowired
    public void setRateLimitService(RateLimitService rateLimitService) {
        DomainRegistry.rateLimitService = rateLimitService;
    }

}
