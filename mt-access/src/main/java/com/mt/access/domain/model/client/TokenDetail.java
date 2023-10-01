package com.mt.access.domain.model.client;

import com.mt.common.domain.model.validate.Checker;
import com.mt.common.domain.model.validate.Validator;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class TokenDetail implements Serializable {

    private static final int MAX_REFRESH_TOKEN_SEC = 60 * 60 * 24 * 366;
    private static final int MIN_REFRESH_TOKEN_SEC = 120;
    private static final int MAX_ACCESS_TOKEN_SEC = 60 * 60 * 24;
    private static final int MIN_ACCESS_TOKEN_SEC = 60;
    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;

    public TokenDetail(Integer accessTokenValiditySeconds, Integer refreshTokenValiditySeconds) {
        setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    private void setAccessTokenValiditySeconds(Integer token) {
        Validator.notNull(token);
        Validator.lessThanOrEqualTo(token, MAX_ACCESS_TOKEN_SEC);
        Validator.greaterThanOrEqualTo(token, MIN_ACCESS_TOKEN_SEC);
        this.accessTokenValiditySeconds = token;
    }

    private void setRefreshTokenValiditySeconds(Integer token) {
        if (Checker.notNull(token)) {
            Validator.lessThanOrEqualTo(token, MAX_REFRESH_TOKEN_SEC);
            Validator.greaterThanOrEqualTo(token, MIN_REFRESH_TOKEN_SEC);
        }
        this.refreshTokenValiditySeconds = token;
    }

}
