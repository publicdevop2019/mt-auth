package com.mt.access.application.token.representation;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mt.access.domain.model.token.JwtToken;
import com.mt.common.domain.model.domain_event.DomainId;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Data;

@Data
public class JwtTokenRepresentation {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("expires_in")
    private Long expiresAt;
    @JsonProperty("iat")
    private Long issueAtSecond;
    @JsonProperty("jti")
    private String tokenId;
    private Set<String> permissionIds;
    private String projectId;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope;
    private Set<String> tenantIds;
    @JsonProperty("token_type")
    private String tokenType;
    private String uid;

    public JwtTokenRepresentation(JwtToken token) {
        this.accessToken = token.getSignedAccessToken();
        this.expiresAt = token.getAccessTokenValidityInSecond();
        this.issueAtSecond = Math.floorDiv(token.getIssueAtMilli(), 1000);
        this.tokenId = token.getId();
        this.permissionIds = token.getPermissionIds().stream().map(DomainId::getDomainId).collect(
            Collectors.toSet());
        this.projectId = token.getProjectId().getDomainId();
        this.refreshToken = token.getSignedRefreshToken();
        this.scope = "not_used";
        this.tenantIds =
            token.getTenantIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet());
        this.tokenType = "bearer";
        this.uid = token.getUserId()==null?null:token.getUserId().getDomainId();
    }
}
