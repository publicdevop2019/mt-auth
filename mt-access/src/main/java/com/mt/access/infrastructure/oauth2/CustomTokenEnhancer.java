package com.mt.access.infrastructure.oauth2;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user_relation.UserRelation;
import com.mt.common.domain.model.domainId.DomainId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * capture issued at time to enable token revocation feature,
 * use user id instead of username to enhance security
 */
@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    @Value("${mt.project.id}")
    private String authProjectId;
    private static final String NOT_USED="not_used";
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        UserId userId = new UserId(authentication.getName());
        Map<String, Object> info = new HashMap<>();
        info.put("iat", Instant.now().getEpochSecond());
        if (!authentication.isClientOnly()) {
            info.put("uid", userId.getDomainId());
        }
        Set<String> scope = authentication.getOAuth2Request().getScope();
        if (scope != null && scope.size() > 0 && !NOT_USED.equals(scope.stream().findFirst().get())) {
            //only one projectId allowed
            Optional<String> first = scope.stream().findFirst();
            Optional<UserRelation> userRelation = ApplicationServiceRegistry.getUserRelationApplicationService().getUserRelation(userId, new ProjectId(first.get()));
            userRelation.ifPresent(e -> {
                info.put("permissionIds", e.getPermissionSnapshot().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                info.put("projectId", e.getProjectId().getDomainId());
            });
        } else {
            Optional<UserRelation> userRelation = ApplicationServiceRegistry.getUserRelationApplicationService().getUserRelation(userId, new ProjectId(authProjectId));
            userRelation.ifPresent(e -> {
                info.put("permissionIds", e.getPermissionSnapshot().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                info.put("projectId", e.getProjectId().getDomainId());
                info.put("tenantId", e.getTenantIds().stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
            });
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
