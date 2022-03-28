package com.mt.access.domain.model.revoke_token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.common.domain.model.domain_id.DomainId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RevokeTokenId extends DomainId {
    public RevokeTokenId(String domainId) {
        super(domainId);
    }

    @JsonIgnore
    public RevokeToken.TokenType getType() {
        return this.getDomainId().indexOf("0C") == 0 ? RevokeToken.TokenType.CLIENT :
            RevokeToken.TokenType.USER;
    }
}
