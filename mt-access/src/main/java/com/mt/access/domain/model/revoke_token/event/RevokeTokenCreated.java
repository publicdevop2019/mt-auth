package com.mt.access.domain.model.revoke_token.event;

import com.mt.access.domain.model.audit.AuditEvent;
import com.mt.access.domain.model.revoke_token.RevokeTokenId;
import com.mt.common.domain.model.domain_event.DomainEvent;

public class RevokeTokenCreated extends DomainEvent implements AuditEvent {
    public static final String name = "REVOKE_TOKEN_CREATED";
    public static final String REVOKE_TOKEN = "revoke_token_created";

    public RevokeTokenCreated(RevokeTokenId revokeTokenId) {
        super(revokeTokenId);
        setTopic(REVOKE_TOKEN);
        setName(name);
    }
}
