package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.JwtService;
import com.mt.proxy.domain.RetrieveJwtPublicKeyService;
import com.nimbusds.jose.jwk.JWKSet;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpRetrieveJwtPublicKeyService implements RetrieveJwtPublicKeyService {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HttpUtility httpUtility;


    @Override
    public JWKSet loadKeys() {
        log.debug("loading jwt keys");
        ResponseEntity<String> exchange = httpUtility.getRestTemplate()
            .exchange(jwtService.getJwtKeyUrl(), HttpMethod.GET, null,
                String.class);
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
