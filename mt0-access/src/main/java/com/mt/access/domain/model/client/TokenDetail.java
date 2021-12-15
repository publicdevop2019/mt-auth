package com.mt.access.domain.model.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@NoArgsConstructor
@Getter
public class TokenDetail implements Serializable {

    private Integer accessTokenValiditySeconds;
    private Integer refreshTokenValiditySeconds;

    public TokenDetail(int accessTokenValiditySeconds, int refreshTokenValiditySeconds) {
        setAccessTokenValiditySeconds(accessTokenValiditySeconds);
        setRefreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    private void setAccessTokenValiditySeconds(Integer accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    private void setRefreshTokenValiditySeconds(Integer refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

}
