package com.mt.proxy.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class RevokeTokenService {

    public boolean checkAccess(String authHeader, String requestURI, Map<String, String> requestBody) throws ParseException {
        if ((authHeader != null && authHeader.contains("Bearer")) ||
                (requestURI.contains("/oauth/token") && requestBody != null && requestBody.get("refresh_token") != null)
        ) {
            String jwtRaw;
            if (authHeader != null && authHeader.contains("Bearer")) {
                jwtRaw = authHeader.replace("Bearer ", "");
            } else {
                jwtRaw = requestBody.get("refresh_token");
            }
            Long issueAt = DomainRegistry.jwtService().getIssueAt(jwtRaw);
            String userId = DomainRegistry.jwtService().getUserId(jwtRaw);
            String clientId = DomainRegistry.jwtService().getClientId(jwtRaw);
            boolean allowUser = true;
            boolean allowClient = true;
            if (userId != null) {
                allowUser = notBlocked(userId, issueAt);
            }
            if (clientId != null) {
                allowClient = notBlocked(clientId, issueAt);
            }
            return allowUser && allowClient;
        }
        return true;
    }

    private boolean notBlocked(String id, Long iat) {
        Optional<RevokeToken> optionalRevokeToken = DomainRegistry.revokeTokenRepository().revokeToken(id);
        return optionalRevokeToken.isEmpty() || optionalRevokeToken.get().getIssuedAt() < iat;
    }
}
