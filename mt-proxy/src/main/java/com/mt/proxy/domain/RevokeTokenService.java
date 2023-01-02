package com.mt.proxy.domain;

import java.text.ParseException;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RevokeTokenService {

    public boolean revoked(String authHeader, String requestUri,
                           Map<String, String> requestBody) {
        if ((authHeader != null && authHeader.contains("Bearer"))
            ||
            (requestUri.contains("/oauth/token") && requestBody != null
                &&
                requestBody.get("refresh_token") != null)
        ) {
            String jwtRaw;
            if (authHeader != null && authHeader.contains("Bearer")) {
                jwtRaw = authHeader.replace("Bearer ", "");
            } else {
                jwtRaw = requestBody.get("refresh_token");
            }

            Long issueAt;
            String userId;
            String clientId;
            try {
                issueAt = DomainRegistry.getJwtService().getIssueAt(jwtRaw);
                userId = DomainRegistry.getJwtService().getUserId(jwtRaw);
                clientId = DomainRegistry.getJwtService().getClientId(jwtRaw);
            } catch (ParseException e) {
                log.error("error during parse", e);
                return true;
            }
            boolean userTokenRevoked = false;
            boolean clientTokenRevoked = false;
            if (userId != null) {
                userTokenRevoked = revoked(userId, issueAt);
            }
            if (clientId != null) {
                clientTokenRevoked = revoked(clientId, issueAt);
            }
            return userTokenRevoked || clientTokenRevoked;
        }
        return false;
    }

    private boolean revoked(String id, Long iat) {
        Optional<RevokeToken> optionalRevokeToken =
            DomainRegistry.getRevokeTokenRepository().revokeToken(id);
        return optionalRevokeToken.isPresent() && optionalRevokeToken.get().getIssuedAt() >= iat;
    }
}
