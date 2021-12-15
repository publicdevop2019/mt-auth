package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.CheckSumService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DomainRegistry {
    private static RevokeTokenRepository revokeTokenRepository;
    private static RevokeTokenService revokeTokenService;
    private static RetrieveEndpointService retrieveEndpointService;
    private static JwtService jwtService;
    private static CheckSumService checkSumService;
    private static RetrieveJwtPublicKeyService retrieveJwtPublicKeyService;

    public static JwtService jwtService() {
        return jwtService;
    }

    public static RetrieveEndpointService retrieveEndpointService() {
        return retrieveEndpointService;
    }

    public static RevokeTokenRepository revokeTokenRepository() {
        return revokeTokenRepository;
    }

    public static RevokeTokenService revokeTokenService() {
        return revokeTokenService;
    }
    public static CheckSumService checkSumService() {
        return checkSumService;
    }

    public static RetrieveJwtPublicKeyService retrieveJwtPublicKeyService() {
        return retrieveJwtPublicKeyService;
    }


    @Autowired
    public void setRetrieveJwtPublicKeyService(RetrieveJwtPublicKeyService retrieveJwtPublicKeyService) {
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

    private static EndpointService endpointService;

    public static EndpointService endpointService() {
        return endpointService;
    }


    @Autowired
    public void setLoadEndpointService(EndpointService roadEndpointService) {
        DomainRegistry.endpointService = roadEndpointService;
    }

}
