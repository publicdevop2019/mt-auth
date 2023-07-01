package com.mt.proxy.domain;

import com.mt.proxy.port.adapter.http.HttpUtility;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtService {
    private static final String JWT_KEY_URL =".well-known/jwks.json";

    private RSAPublicKey publicKey;
    @Autowired
    private HttpUtility httpHelper;
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return new NimbusReactiveJwtDecoder(getJwtKeyUrl());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadKeys() {
        JWKSet jwkSet = DomainRegistry.getRetrieveJwtPublicKeyService().loadKeys();
        JWK jwk = jwkSet.getKeys().get(0);
        try {
            publicKey = jwk.toRSAKey().toRSAPublicKey();
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }
    public String getJwtKeyUrl() {
        return httpHelper.resolveAccessPath() + JWT_KEY_URL;
    }
    public boolean verify(String jwt) {
        SignedJWT parse;
        try {
            parse = SignedJWT.parse(jwt);
        } catch (ParseException e) {
            log.error("error during parse signed jwt", e);
            return false;
        }
        try {
            return parse.verify(new RSASSAVerifier(publicKey));
        } catch (JOSEException e) {
            log.error("error during validate jwt", e);
            return false;
        }

    }

    public Set<String> getResourceIds(String jwtRaw) throws ParseException {
        return getClaims(jwtRaw, "aud");
    }

    public Set<String> getScopes(String jwtRaw) throws ParseException {
        return getClaims(jwtRaw, "scope");
    }

    public Set<String> getPermissionIds(String jwtRaw) throws ParseException {
        return getClaims(jwtRaw, "permissionIds");
    }

    public Long getIssueAt(String jwtRaw) throws ParseException {
        return Long.parseLong(getClaims(jwtRaw, "iat").stream().findAny().get());
    }

    public String getUserId(String jwtRaw) throws ParseException {
        if (isUser(jwtRaw)) {
            return getClaims(jwtRaw, "uid").stream().findAny().get();
        }
        return null;
    }

    public String getClientId(String jwtRaw) throws ParseException {
        return getClaims(jwtRaw, "client_id").stream().findAny().get();
    }

    public boolean isUser(String jwtRaw) throws ParseException {
        Set<String> uid = getClaims(jwtRaw, "uid");
        return !uid.isEmpty() && !uid.contains(null);
    }

    private Set<String> getClaims(String jwtRaw, String field) throws ParseException {
        JWT jwt = JWTParser.parse(jwtRaw);
        JWTClaimsSet jwtClaimsSet = jwt.getJWTClaimsSet();
        log.trace("getting clain for {}", field);
        if (jwtClaimsSet.getClaim(field) instanceof String) {
            String claim = (String) jwtClaimsSet.getClaim(field);
            Set<String> objects = new HashSet<>();
            objects.add(claim);
            return objects;
        }
        if (jwtClaimsSet.getClaim(field) instanceof Long) {
            return new HashSet<>(List.of(((Long) jwtClaimsSet.getClaim(field)).toString()));
        }
        if (jwtClaimsSet.getClaim(field) == null) {
            return Collections.emptySet();
        }
        if (jwtClaimsSet.getClaim(field) instanceof Date) {
            long epochSecond = ((Date) jwtClaimsSet.getClaim(field)).toInstant().getEpochSecond();
            return new HashSet<>(List.of(String.valueOf(epochSecond)));
        }
        List<String> resourceIds = (List<String>) jwtClaimsSet.getClaim(field);
        return new HashSet<>(resourceIds);
    }

    public String getProjectId(String jwtRaw) throws ParseException {
        return getClaims(jwtRaw, "projectId").stream().findAny().get();
    }
}
