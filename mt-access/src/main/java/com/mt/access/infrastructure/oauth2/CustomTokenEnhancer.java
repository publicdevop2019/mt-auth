package com.mt.access.infrastructure.oauth2;

import static com.mt.access.infrastructure.JwtCurrentUserService.TENANT_IDS;
import static com.mt.common.domain.model.jwt.JwtUtility.JWT_CLAIM_PERM;
import static com.mt.common.domain.model.jwt.JwtUtility.JWT_CLAIM_UID;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.client.Client;
import com.mt.access.domain.model.client.ClientId;
import com.mt.access.domain.model.permission.PermissionId;
import com.mt.access.domain.model.project.ProjectId;
import com.mt.access.domain.model.role.Role;
import com.mt.access.domain.model.role.RoleId;
import com.mt.access.domain.model.user.UserId;
import com.mt.access.domain.model.user.UserRelation;
import com.mt.access.infrastructure.AppConstant;
import com.mt.common.domain.model.domain_event.DomainId;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;

/**
 * capture issued at time to enable token revocation feature,
 * use user id instead of username to enhance security.
 */
@Slf4j
@Component
public class CustomTokenEnhancer implements TokenEnhancer {
    private static final String NOT_USED = "not_used";
    private static final String PROJECT_ID = "projectId";

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken,
                                     OAuth2Authentication authentication) {
        Map<String, Object> info = new HashMap<>();
        info.put("iat", Instant.now().getEpochSecond());
        if (!authentication.isClientOnly()) {
            UserId userId = new UserId(authentication.getName());
            info.put(JWT_CLAIM_UID, userId.getDomainId());
            //for user
            Set<String> scope = authentication.getOAuth2Request().getScope();
            if (scope != null && scope.size() > 0
                &&
                !NOT_USED.equals(scope.stream().findFirst().get())) {
                //only one projectId allowed
                //get tenant project permission
                Optional<String> first = scope.stream().findFirst();
                ProjectId projectId = new ProjectId(first.get());
                Optional<UserRelation> userRelation =
                    ApplicationServiceRegistry.getUserRelationApplicationService()
                        .query(userId, projectId);
                if (userRelation.isEmpty()) {
                    //auto assign default user role for target project
                    UserRelation newRelation =
                        ApplicationServiceRegistry.getUserRelationApplicationService()
                            .internalOnboardUserToTenant(userId, projectId);
                    log.debug("new tenant user relation for token is {}", newRelation);
                    Set<PermissionId> compute =
                        DomainRegistry.getComputePermissionService().compute(newRelation);
                    info.put(JWT_CLAIM_PERM,
                        compute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                    info.put(PROJECT_ID, newRelation.getProjectId().getDomainId());
                } else {
                    log.debug("tenant user relation for token is {}", userRelation.get());
                    Set<PermissionId> compute =
                        DomainRegistry.getComputePermissionService().compute(userRelation.get());
                    info.put(JWT_CLAIM_PERM,
                        compute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                    info.put(PROJECT_ID, userRelation.get().getProjectId().getDomainId());
                }
            } else {
                //get auth project permission and user tenant projects
                Optional<UserRelation> userRelation =
                    ApplicationServiceRegistry.getUserRelationApplicationService()
                        .query(userId, new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
                userRelation.ifPresent(
                    relation -> log.debug("auth user relation for token is {}", relation));
                userRelation.ifPresent(relation -> {
                    Set<PermissionId> compute =
                        DomainRegistry.getComputePermissionService().compute(relation);
                    info.put(JWT_CLAIM_PERM,
                        compute.stream().map(DomainId::getDomainId).collect(Collectors.toSet()));
                    info.put(PROJECT_ID, relation.getProjectId().getDomainId());
                    if (relation.getTenantIds() != null) {
                        info.put(TENANT_IDS,
                            relation.getTenantIds().stream().map(DomainId::getDomainId)
                                .collect(Collectors.toSet()));
                    }
                });
            }
        } else {
            //for client
            ClientId clientId = new ClientId(authentication.getName());
            Client client =
                ApplicationServiceRegistry.getClientApplicationService().internalQuery(clientId);
            RoleId roleId = client.getRoleId();
            Role byId =
                ApplicationServiceRegistry.getRoleApplicationService().internalQueryById(roleId);
            info.put(PROJECT_ID, client.getProjectId().getDomainId());
            info.put(JWT_CLAIM_PERM,
                byId.getTotalPermissionIds().stream().map(DomainId::getDomainId)
                    .collect(Collectors.toSet()));
        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);
        return accessToken;
    }
}
