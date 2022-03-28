package com.mt.access.domain.model.client;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TokenDetail that = (TokenDetail) o;
        return Objects.equals(accessTokenValiditySeconds, that.accessTokenValiditySeconds)
            &&
            Objects.equals(refreshTokenValiditySeconds, that.refreshTokenValiditySeconds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessTokenValiditySeconds, refreshTokenValiditySeconds);
    }
}
