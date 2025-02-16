package com.mt.access.domain.model;

import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.revoke_token.RevokeToken;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;
import com.mt.access.domain.model.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RevokeTokenValidationService {
    public void checkToken(RevokeTokenId tokenId) {
        RevokeToken.TokenType type = tokenId.getType();
        if (RevokeToken.TokenType.CLIENT.equals(type)) {
            ClientId clientId = new ClientId(tokenId.getDomainId());
            DomainRegistry.getClientRepository().get(clientId);
        } else {
            UserId userId = new UserId(tokenId.getDomainId());
            DomainRegistry.getUserRepository().get(userId);
        }
    }
}
