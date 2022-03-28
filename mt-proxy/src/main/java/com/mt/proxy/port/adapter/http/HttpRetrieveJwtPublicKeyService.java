package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.JwtService;
import com.mt.proxy.domain.RetrieveJwtPublicKeyService;
import com.nimbusds.jose.jwk.JWKSet;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class HttpRetrieveJwtPublicKeyService implements RetrieveJwtPublicKeyService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StandardEnvironment environment;
    @Autowired
    private JwtService jwtService;

    @Override
    public JWKSet loadKeys() {
        ResponseEntity<String> exchange = restTemplate
            .exchange(jwtService.resolveAccessUrl(), HttpMethod.GET, null, String.class);
        try {
            return JWKSet.parse(exchange.getBody());
        } catch (ParseException e) {
            log.error("error during parse jwk", e);
            throw new UnableRetrieveJwkException();
        }
    }

    private static class UnableRetrieveJwkException extends RuntimeException {
    }
}
