package com.mt.proxy.domain;

import com.mt.proxy.infrastructure.LogService;
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
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtService {
    private static final String JWT_KEY_URL = "/.well-known/jwks.json";

    private RSAPublicKey publicKey;
    @Autowired
    private HttpUtility httpHelper;
    @Autowired
    private LogService logService;
    @Autowired
    private InstanceInfo instanceInfo;

    public void loadKeys() {
        if (Boolean.TRUE.equals(instanceInfo.getJwtPublicCertLoaded())) {
            return;
        }
        synchronized (JwtService.class) {
            if (Boolean.TRUE.equals(instanceInfo.getJwtPublicCertLoaded())) {
                return;
            }
            logService.initTrace();
            JWKSet jwkSet = DomainRegistry.getRetrieveJwtPublicKeyService().loadKeys();
            JWK jwk = jwkSet.getKeys().get(0);
            try {
                publicKey = jwk.toRSAKey().toRSAPublicKey();
            } catch (JOSEException e) {
                log.warn("error during public key load", e);
            }
            instanceInfo.setJwtPublicCertLoaded(true);
        }
    }

    public String getJwtKeyUrl() {
        return httpHelper.getAccessUrl() + JWT_KEY_URL;
    }

    public boolean verifyBearer(String jwt) {
        jwt = jwt.replace("Bearer ", "");
        SignedJWT parse;
        try {
            parse = SignedJWT.parse(jwt);
        } catch (ParseException e) {
            log.error("error during parse signed jwt", e);
            return false;
        }
        boolean verify;
        try {
            verify = parse.verify(new RSASSAVerifier(publicKey));
        } catch (JOSEException e) {
            log.error("error during validate jwt", e);
            return false;
        }
        if (!verify) {
            return false;
        }
        Long expSec = (Long) parse.getPayload().toJSONObject().get("exp");
        if (expSec == null) {
            return false;
        }
        long currentMilli = System.currentTimeMillis();
        return currentMilli <= expSec * 1000;
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
        log.trace("getting claim for {}", field);
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
