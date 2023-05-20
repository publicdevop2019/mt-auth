package com.mt.proxy.port.adapter.http;

import com.mt.proxy.domain.JwtService;
import com.mt.proxy.domain.RetrieveJwtPublicKeyService;
import com.nimbusds.jose.jwk.JWKSet;
import java.text.ParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HttpRetrieveJwtPublicKeyService implements RetrieveJwtPublicKeyService {
    @Value("${manytree.url.jwtKey}")
    private String jwtKeyUrl;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HttpUtility httpHelper;

    @Override
    public JWKSet loadKeys() {
        ResponseEntity<String> exchange = httpHelper.getRestTemplate()
            .exchange(jwtKeyUrl, HttpMethod.GET, null, String.class);
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
