package com.mt.access.application.revoke_token;

import com.mt.access.domain.model.revoke_token.RevokeToken;
import lombok.Data;

@Data
public class RevokeTokenCardRepresentation {
    private String targetId;
    private Long issuedAt;
    private RevokeToken.TokenType type;

    public RevokeTokenCardRepresentation(RevokeToken token) {
        targetId = token.getRevokeTokenId().getDomainId();
        issuedAt = token.getIssuedAt();
        type = token.getRevokeTokenId().getType();
    }
}
