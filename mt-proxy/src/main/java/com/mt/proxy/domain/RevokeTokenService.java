package com.mt.proxy.domain;

import java.text.ParseException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RevokeTokenService {

    public static final String BEARER_PREFIX = "Bearer ";

    public boolean revoked(String token) {
        if (token == null) {
            return false;
        }
        String jwtRaw = token.replace(BEARER_PREFIX, "");
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

    private boolean revoked(String id, Long iat) {
        Optional<RevokeToken> optionalRevokeToken =
            DomainRegistry.getRevokeTokenRepository().revokeToken(id);
        return optionalRevokeToken.isPresent() && optionalRevokeToken.get().getIssuedAt() >= iat;
    }
}
