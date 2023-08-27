package com.mt.access.application.token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.token.representation.JwtTokenRepresentation;
import com.mt.access.application.user.representation.UserSpringRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.token.JwtToken;
import com.mt.access.domain.model.user.CurrentPassword;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.util.OAuth2Utils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenApplicationService {

    private static final String MFA_REQUIRED = "mfa required";

    public ResponseEntity<?> grantToken(String clientId, Map<String, String> parameters,
                                        String agentInfo, String clientIpAddress) {
        log.debug("customize token flow");
        boolean invalidParams = false;
        if (Checker.notNull(parameters.get("refresh_token")) &&
            !parameters.get("grant_type").equalsIgnoreCase("refresh_token")
        ) {
            invalidParams = true;
        }
        if (Checker.isNull(parameters.get("refresh_token")) &&
            parameters.get("grant_type").equalsIgnoreCase("refresh_token")
        ) {
            invalidParams = true;
        }
        if (Checker.notNull(parameters.get("username")) &&
            !parameters.get("grant_type").equalsIgnoreCase("password")) {
            invalidParams = true;
        }
        if (parameters.get("grant_type").equalsIgnoreCase("password")) {
            if (Checker.isNull(parameters.get("username"))) {
                invalidParams = true;
            }
        }
        if (invalidParams) {
            throw new DefinedRuntimeException("invalid token params", "1089",
                HttpResponseCode.BAD_REQUEST);
        }
        //if password grant then do mfa check
        if (parameters.get("grant_type").equalsIgnoreCase("password")) {
            log.debug("checking user mfa");
            LoginResult loginResult = ApplicationServiceRegistry.getUserApplicationService()
                .userLoginCheck(clientIpAddress, agentInfo, parameters.get("username"),
                    parameters.get("mfa_code"),
                    parameters.get("mfa_id"));
            if (Checker.isFalse(loginResult.getAllowed())) {
                if (Checker.isTrue(loginResult.getInvalidMfa())) {
                    log.debug("invalid mfa");
                    return ResponseEntity.badRequest().build();
                } else {
                    log.debug("asking mfa");
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put("message", MFA_REQUIRED);
                    stringStringHashMap.put("mfaId", loginResult.getMfaId().getValue());
                    return ResponseEntity.ok().body(stringStringHashMap);
                }
            }
        }
        UserDetails userDetails = null;
        if (Checker.notNull(parameters.get("username"))) {
            userDetails = ApplicationServiceRegistry.getUserApplicationService()
                .loadUserByUsername(parameters.get("username"));
            if (!userDetails.isAccountNonLocked()) {
                throw new DefinedRuntimeException("invalid token params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
            UserSpringRepresentation userDetails1 = (UserSpringRepresentation) userDetails;
            if (!DomainRegistry.getEncryptionService()
                .compare(userDetails1.getUserPassword(),
                    new CurrentPassword(parameters.get("password")))) {
                throw new DefinedRuntimeException("wrong password", "1000",
                    HttpResponseCode.BAD_REQUEST);
            }
        }
        ClientDetails clientDetails =
            ApplicationServiceRegistry.getClientApplicationService()
                .loadClientByClientId(clientId);
        if (parameters.get("grant_type").equalsIgnoreCase("refresh_token")) {
            if (!clientDetails.getAuthorizedGrantTypes().contains("refresh_token")) {
                throw new DefinedRuntimeException("invalid token params", "1089",
                    HttpResponseCode.BAD_REQUEST);
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
    }

    /**
     * consume authorize request.
     *
     * @param parameters request params
     * @return authorization response params
     */
    public String authorize(Map<String, String> parameters) {
        String clientId = parameters.get("client_id");
        String responseType = parameters.get("response_type");
        String redirectUri = parameters.get("redirect_uri");
        Validator.notNull(clientId);
        Validator.notNull(responseType);
        Validator.notNull(redirectUri);
        if (!"code".equalsIgnoreCase(responseType)) {
            throw new DefinedRuntimeException("unsupported response types: " + responseType,
                "1006",
                HttpResponseCode.BAD_REQUEST);
        }

        ClientDetails client = ApplicationServiceRegistry.getClientApplicationService()
            .loadClientByClientId(clientId);

        if (client == null) {
            throw new DefinedRuntimeException(
                "unable to find authorize client " + parameters.get(OAuth2Utils.CLIENT_ID), "1005",
                HttpResponseCode.BAD_REQUEST);
        }
        if (!client.getRegisteredRedirectUri().contains(redirectUri)) {
            throw new DefinedRuntimeException(
                "unknown redirect url", "1008", HttpResponseCode.BAD_REQUEST);
        }
        return DomainRegistry.getTokenService()
            .authorize(
                redirectUri, clientId, Collections.singleton(parameters.get("project_id")),
                new ProjectId(parameters.get("project_id")),
                DomainRegistry.getCurrentUserService().getPermissionIds(),
                DomainRegistry.getCurrentUserService().getUserId()
            );
    }
}
