package com.mt.access.domain.model.revoke_token;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mt.access.domain.model.client.ClientId;
import com.mt.common.domain.model.domain_event.DomainId;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RevokeTokenId extends DomainId {
    public RevokeTokenId(String domainId) {
        super(domainId);
    }

    @JsonIgnore
    public RevokeToken.TokenType getType() {
        return this.getDomainId().indexOf(ClientId.getClientPrefix()) == 0 ?
            RevokeToken.TokenType.CLIENT :
            RevokeToken.TokenType.USER;
    }
}
