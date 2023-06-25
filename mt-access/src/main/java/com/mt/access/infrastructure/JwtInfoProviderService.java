package com.mt.access.infrastructure;

import com.nimbusds.jose.jwk.JWKSet;
import java.security.KeyPair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JwtInfoProviderService {
    @Autowired
    private KeyPair keyPair;

    @Autowired
    private JWKSet jwkSet;

    public KeyPair getKeyPair() {
        return keyPair;
    }

    public JWKSet getPublicKeys() {
        return jwkSet;
    }
}
