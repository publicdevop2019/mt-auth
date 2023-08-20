package com.mt.access.application.token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.token.representation.JwtTokenRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.token.JwtToken;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.validate.Checker;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenApplicationService {

    public ResponseEntity<?> grantToken(String clientId, Map<String, String> parameters,
                                        String agentInfo, String clientIpAddress) {
        LoginResult loginResult = ApplicationServiceRegistry.getUserApplicationService()
            .userLogin(clientIpAddress, agentInfo, parameters.get("grant_type"),
                parameters.get("username"), parameters.get("mfa_code"), parameters.get("mfa_id"));
        if (Checker.isTrue(loginResult.getAllowed())) {
            log.info("customize password token flow");
            if (Checker.notNull(parameters.get("refresh_token")) &&
                !parameters.get("grant_type").equalsIgnoreCase("refresh_token")
            ) {
                throw new DefinedRuntimeException("invalid token params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (Checker.isNull(parameters.get("refresh_token")) &&
                parameters.get("grant_type").equalsIgnoreCase("refresh_token")
            ) {
                throw new DefinedRuntimeException("invalid token params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (Checker.notNull(parameters.get("username")) &&
                !parameters.get("grant_type").equalsIgnoreCase("password")) {
                throw new DefinedRuntimeException("invalid token params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (parameters.get("grant_type").equalsIgnoreCase("password")) {
                if (Checker.isNull(parameters.get("username"))) {
                    throw new DefinedRuntimeException("invalid token params", "1089",
                        HttpResponseCode.BAD_REQUEST);
                }
            }
            UserDetails userDetails = null;
            if (Checker.notNull(parameters.get("username"))) {
                userDetails = ApplicationServiceRegistry.getUserApplicationService()
                    .loadUserByUsername(parameters.get("username"));
            }
            ClientDetails clientDetails =
                ApplicationServiceRegistry.getClientApplicationService()
                    .loadClientByClientId(clientId);
            if (parameters.get("grant_type").equalsIgnoreCase("refresh_token")) {
                if (!clientDetails.getAuthorizedGrantTypes().contains("refresh_token")) {
                    throw new DefinedRuntimeException("invalid token params", "1089",
                        HttpResponseCode.UNAUTHORIZED);
                }
                Integer expInSec = JwtUtility.getField("exp", parameters.get("refresh_token"));
                if (Instant.now().isAfter(Instant.ofEpochSecond(expInSec))) {
                    throw new DefinedRuntimeException("refresh token expired", "1090",
                        HttpResponseCode.UNAUTHORIZED);
                }
            }
            JwtToken token =
                DomainRegistry.getTokenService().grant(parameters, clientDetails, userDetails);
            return ResponseEntity.ok(new JwtTokenRepresentation(token));
        } else {
            if (Checker.isTrue(loginResult.getInvalidMfa())) {
                return ResponseEntity.badRequest().build();
            } else {
                HashMap<String, String> stringStringHashMap = new HashMap<>();
                stringStringHashMap.put("message", "mfa required");
                stringStringHashMap.put("mfaId", loginResult.getMfaId().getValue());
                return ResponseEntity.ok().body(stringStringHashMap);
            }
        }
    }
}
