package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.RetrieveJwtPublicKeyService;
import com.nimbusds.jose.jwk.JWKSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TranslatingJwtPublicKeyService implements RetrieveJwtPublicKeyService {
    @Autowired
    private JwtPublicKeyAdapter jwtPublicKeyAdapter;

    @Override
    public JWKSet loadKeys() {
        log.debug("load jwt public keys");
        JWKSet s = jwtPublicKeyAdapter.fetchKeys();
        log.debug("load jwt public keys complete");
        return s;
    }
}
