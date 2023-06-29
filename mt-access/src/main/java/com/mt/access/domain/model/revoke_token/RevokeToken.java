package com.mt.access.domain.model.revoke_token;

import java.time.Instant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@NoArgsConstructor
@EqualsAndHashCode
public class RevokeToken {
    public static final String ENTITY_TARGET_ID = "targetId";
    public static final String ENTITY_ISSUE_AT = "issuedAt";
    private Long issuedAt;
    private RevokeTokenId revokeTokenId;

    public RevokeToken(RevokeTokenId revokeTokenId) {
        this.issuedAt = Instant.now().getEpochSecond();
        this.revokeTokenId = revokeTokenId;
    }


    public enum TokenType {
        CLIENT,
        USER
    }
}
