package com.mt.proxy.domain;

import com.netflix.discovery.EurekaClient;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.stereotype.Service;

import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.*;

@Slf4j
@Service
public class JwtService {
    @Value("${manytree.mt-access.appId}")
    private String appName;
    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwtKeyUrl;
    @Autowired
    private EurekaClient eurekaClient;
    private RSAPublicKey publicKey;

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        String url = resolveAccessUrl();
        return new NimbusReactiveJwtDecoder(url);
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

    public String resolveAccessUrl() {
        if (eurekaClient.getApplication(appName) != null) {
            log.debug("update property value with resolve access path");
            String homePageUrl = eurekaClient.getApplication(appName).getInstances().get(0).getHomePageUrl();
            return homePageUrl + jwtKeyUrl;

        } else {
            log.error("jwt public key config update failed due to service is not ready");
            throw new IllegalStateException("jwt public key config update failed due to service is not ready");
        }
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

    public Set<String> getRoles(String jwtRaw) throws ParseException {
        return getClaims(jwtRaw, "authorities");
    }

    public Long getIssueAt(String jwtRaw) throws ParseException {
        return Long.parseLong(getClaims(jwtRaw, "iat").stream().findAny().get());
    }

    public String getUserId(String jwtRaw) throws ParseException {
        if (isUser(jwtRaw))
            return getClaims(jwtRaw, "uid").stream().findAny().get();
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
        if (jwtClaimsSet.getClaim(field) instanceof Long)
            return new HashSet<>(List.of(((Long) jwtClaimsSet.getClaim(field)).toString()));
        if (jwtClaimsSet.getClaim(field) == null)
            return Collections.emptySet();
        if (jwtClaimsSet.getClaim(field) instanceof Date) {
            long epochSecond = ((Date) jwtClaimsSet.getClaim(field)).toInstant().getEpochSecond();
            return new HashSet<>(List.of(String.valueOf(epochSecond)));
        }
        List<String> resourceIds = (List<String>) jwtClaimsSet.getClaim(field);
        return new HashSet<>(resourceIds);
    }
}
