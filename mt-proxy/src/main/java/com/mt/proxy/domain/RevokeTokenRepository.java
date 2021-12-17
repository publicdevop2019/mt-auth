package com.mt.proxy.domain;

import java.util.Optional;

public interface RevokeTokenRepository {
    Optional<RevokeToken> revokeToken(String id);
}
