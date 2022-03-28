package com.mt.proxy.domain;

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

}
