package com.mt.proxy.port.adapter.http;

import com.nimbusds.jose.jwk.JWKSet;

public interface JwtPublicKeyAdapter {
    JWKSet fetchKeys();
}
