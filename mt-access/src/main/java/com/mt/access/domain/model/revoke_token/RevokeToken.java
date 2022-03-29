package com.mt.access.domain.model.revoke_token;

import com.google.common.base.Objects;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
public class RevokeToken {
    public static final String ENTITY_TARGET_ID = "targetId";
    public static final String ENTITY_ISSUE_AT = "issuedAt";
    private Long issuedAt;
    private RevokeTokenId revokeTokenId;

    public RevokeToken(RevokeTokenId revokeTokenId) {
        this.issuedAt = Instant.now().getEpochSecond();
        this.revokeTokenId = revokeTokenId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RevokeToken)) {
            return false;
        }
        RevokeToken token = (RevokeToken) o;
        return Objects.equal(revokeTokenId, token.revokeTokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(revokeTokenId);
    }

    public enum TokenType {
        CLIENT,
        USER
    }
}
