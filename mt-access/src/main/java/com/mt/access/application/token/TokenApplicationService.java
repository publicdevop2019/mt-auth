package com.mt.access.application.token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.representation.ClientSpringOAuth2Representation;
import com.mt.access.application.token.representation.JwtTokenRepresentation;
import com.mt.access.application.user.representation.UserSpringRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.token.JwtToken;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.common.domain.CommonDomainRegistry;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenApplicationService {

    private static final String MFA_REQUIRED = "mfa required";

    public ResponseEntity<?> grantToken(String clientId, String clientSecret,
                                        Map<String, String> parameters,
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
            AtomicReference<LoginResult> loginResult = new AtomicReference<>();
            CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
                loginResult.set(ApplicationServiceRegistry.getUserApplicationService()
                    .userLoginCheck(clientIpAddress, agentInfo, parameters.get("username"),
                        parameters.get("mfa_code"),
                        parameters.get("mfa_id")));
            });
            if (Checker.isFalse(loginResult.get().getAllowed())) {
                if (Checker.isTrue(loginResult.get().getInvalidMfa())) {
                    log.debug("invalid mfa");
                    return ResponseEntity.badRequest().build();
                } else {
                    log.debug("asking mfa");
                    HashMap<String, String> stringStringHashMap = new HashMap<>();
                    stringStringHashMap.put("message", MFA_REQUIRED);
                    stringStringHashMap.put("mfaId", loginResult.get().getMfaId().getValue());
                    return ResponseEntity.ok().body(stringStringHashMap);
                }
            }
        }
        UserSpringRepresentation userDetails;
        if (Checker.notNull(parameters.get("username"))) {
            userDetails = ApplicationServiceRegistry.getUserApplicationService()
                .loadUserByUsername(parameters.get("username"));
            if (!userDetails.isAccountNonLocked()) {
                throw new DefinedRuntimeException("invalid token params", "1089",
                    HttpResponseCode.BAD_REQUEST);
            }
            if (!DomainRegistry.getEncryptionService()
                .compare(parameters.get("password"),
                    userDetails.getUserPassword().getPassword())) {
                throw new DefinedRuntimeException("wrong password", "1000",
                    HttpResponseCode.BAD_REQUEST);
            }
        } else {
            userDetails = null;
        }
        ClientSpringOAuth2Representation clientDetails =
            ApplicationServiceRegistry.getClientApplicationService()
                .loadClientByClientId(clientId);
        if (clientDetails == null) {
            throw new DefinedRuntimeException("client not found", "1091",
                HttpResponseCode.UNAUTHORIZED);
        }
        if (!DomainRegistry.getEncryptionService()
            .compare(clientSecret,
                clientDetails.getClientSecret())
        ) {
            throw new DefinedRuntimeException("wrong client password", "1070",
                HttpResponseCode.UNAUTHORIZED);
        }
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
        AtomicReference<JwtToken> token = new AtomicReference<>();
        CommonDomainRegistry.getTransactionService().transactionalEvent((context) -> {
            token.set(
                DomainRegistry.getTokenService().grant(parameters, clientDetails, userDetails));
        });
        return ResponseEntity.ok(new JwtTokenRepresentation(token.get()));
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

        ClientSpringOAuth2Representation client =
            ApplicationServiceRegistry.getClientApplicationService()
                .loadClientByClientId(clientId);

        if (client == null) {
            throw new DefinedRuntimeException(
                "unable to find authorize client", "1005",
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
