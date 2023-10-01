package com.mt.access.domain.model.client;

import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.RoleId;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * projection entity
 * 1. used for oauth login
 * 2. read only
 * 3. eager load
 * 4. subset of client
 */
@Data
@NoArgsConstructor
public class LoginOAuthClient {
    @Getter(AccessLevel.PRIVATE)
    private Long id;

    @Getter
    private final Set<ClientId> resources = new HashSet<>();

    @Getter
    private final Set<ClientId> externalResources = new HashSet<>();

    @Getter
    private ProjectId projectId;

    @Getter
    private RoleId roleId;

    @Getter
    private ClientId clientId;

    @Getter
    private String name;

    @Getter
    private String path;

    @Getter
    private ExternalUrl externalUrl;

    @Getter
    private String secret;

    @Getter
    private String description;

    @Getter
    private Set<ClientType> types=new HashSet<>();

    @Getter
    private Boolean accessible;

    @Getter
    private OAuthRedirectDetail authorizationCodeGrant;

    @Getter
    private Set<GrantType> grantTypes=new HashSet<>();

    @Getter
    private TokenDetail tokenDetail;

    @Data
    @NoArgsConstructor
    public static class OAuthRedirectDetail{

        @Getter
        private final Set<RedirectUrl> redirectUrls = new HashSet<>();

        @Getter
        @Setter
        private Boolean autoApprove = false;
    }

    public Integer accessTokenValiditySeconds() {
        if (tokenDetail == null) {
            return null;
        }
        return tokenDetail.getAccessTokenValiditySeconds();
    }

    public int getRefreshTokenValiditySeconds() {
        if (grantTypes.contains(GrantType.PASSWORD)
            &&
            grantTypes.contains(GrantType.REFRESH_TOKEN)) {
            return getTokenDetail().getRefreshTokenValiditySeconds();
        }
        return 0;
    }
    public boolean getAutoApprove() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getAuthorizationCodeGrant().getAutoApprove();
        }
        return false;
    }
    public Set<String> getRegisteredRedirectUri() {
        if (grantTypes.contains(GrantType.AUTHORIZATION_CODE)) {
            return getAuthorizationCodeGrant().getRedirectUrls().stream().map(RedirectUrl::getValue)
                .collect(Collectors.toSet());
        }
        return Collections.emptySet();

    }
}
