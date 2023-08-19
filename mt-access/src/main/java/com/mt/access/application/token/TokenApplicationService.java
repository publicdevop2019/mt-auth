package com.mt.access.application.token;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.token.representation.JwtTokenRepresentation;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.token.JwtToken;
import com.mt.access.domain.model.user.LoginResult;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import com.mt.common.domain.model.validate.Checker;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

@Slf4j
@Service
public class TokenApplicationService {
    @Autowired
    private TokenEndpoint tokenEndpoint;

    public ResponseEntity<?> grantToken(Principal principal, Map<String, String> parameters,
                                        String agentInfo, String clientIpAddress) {
        LoginResult loginResult = ApplicationServiceRegistry.getUserApplicationService()
            .userLogin(clientIpAddress, agentInfo, parameters.get("grant_type"),
                parameters.get("username"), parameters.get("mfa_code"), parameters.get("mfa_id"));
        String clientId = principal.getName();
        if (Checker.isTrue(loginResult.getAllowed())) {
            if ("password".equalsIgnoreCase(parameters.get("grant_type"))) {
                log.info("customize password token flow");
                //TODO validation client id
                //TODO validation refresh token if refresh grant
                //TODO validate scope?
                //TODO validate grant type
                UserDetails username = null;
                if (parameters.get("grant_type").equalsIgnoreCase("password")) {
                    if (parameters.get("username") == null) {
                        throw new DefinedRuntimeException("invalid token params", "1089", HttpResponseCode.BAD_REQUEST);
                    }
                    username = ApplicationServiceRegistry.getUserApplicationService()
                        .loadUserByUsername(parameters.get("username"));
                }
                ClientDetails clientDetails =
                    ApplicationServiceRegistry.getClientApplicationService()
                        .loadClientByClientId(clientId);
                JwtToken token =
                    DomainRegistry.getTokenService().grant(parameters, clientDetails, username);
                return ResponseEntity.ok(new JwtTokenRepresentation(token));
            } else {
                try {
                    return tokenEndpoint.postAccessToken(principal, parameters);
                } catch (HttpRequestMethodNotSupportedException e) {
                    throw new RuntimeException(e);
                }
            }
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
