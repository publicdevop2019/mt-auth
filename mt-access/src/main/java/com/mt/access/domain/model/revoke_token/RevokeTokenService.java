package com.mt.access.domain.model.revoke_token;

import com.mt.common.domain.model.domain_id.DomainId;

public interface RevokeTokenService {
    void revokeToken(DomainId domainId);
}
