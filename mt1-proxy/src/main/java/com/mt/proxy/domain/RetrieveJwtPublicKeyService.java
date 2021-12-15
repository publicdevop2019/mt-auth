package com.mt.proxy.domain;

import com.nimbusds.jose.jwk.JWKSet;

public interface RetrieveJwtPublicKeyService {
    JWKSet loadKeys();
}
