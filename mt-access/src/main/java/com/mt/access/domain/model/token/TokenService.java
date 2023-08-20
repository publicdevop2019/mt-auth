package com.mt.access.domain.model.token;

import static com.mt.access.domain.model.ticket.TicketInfo.PERMISSION_IDS;
import static com.mt.access.domain.model.ticket.TicketInfo.USER_ID;
import static com.mt.access.infrastructure.JwtCurrentUserService.TENANT_IDS;

import com.mt.access.application.ApplicationServiceRegistry;
import com.mt.access.application.client.representation.ClientSpringOAuth2Representation;
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
import com.mt.access.infrastructure.JwtInfoProviderService;
import com.mt.common.domain.model.domain_event.DomainId;
import com.mt.common.domain.model.jwt.JwtUtility;
import com.mt.common.domain.model.validate.Checker;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.KeyPair;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TokenService {
    @Autowired
    private JwtInfoProviderService jwtInfoProviderService;
    private static final String NOT_USED = "not_used";
    private static final String CLIENT_ID = "client_id";
    private static final String PROJECT_ID = "projectId";

    public JwtToken grant(Map<String, String> parameters,
                          ClientDetails clientDetails, UserDetails userDetails) {
        String scope = parameters.get("scope");
        if ("client_credentials".equalsIgnoreCase(parameters.get("grant_type"))
            ||
            "password".equalsIgnoreCase(parameters.get("grant_type"))
        ) {
            return grantToken(clientDetails, userDetails,
                scope != null ? Collections.singleton(scope) : Collections.emptySet());
        } else if ("refresh_token".equalsIgnoreCase(parameters.get("grant_type"))) {
            return grantRefreshToken(clientDetails, parameters.get("refresh_token"));
        } else if ("authorization_code".equalsIgnoreCase(parameters.get("grant_type"))) {
            return grantAuthorizationCode(clientDetails);
        } else {
            return null;
        }
    }

    private JwtToken grantToken(ClientDetails clientDetails, @Nullable UserDetails userDetails,
                                Set<String> scope) {
        final boolean isClient = Checker.isNull(userDetails);
        ClientSpringOAuth2Representation clientDetail =
            (ClientSpringOAuth2Representation) clientDetails;
        final boolean hasRefresh = clientDetail.getAuthorizedGrantTypes().contains("refresh_token");
        ClientId clientId = new ClientId(clientDetail.getClientId());
        //for user & client
        ProjectId projectId = null;
        Set<PermissionId> permissionIds = Collections.emptySet();
        Set<ProjectId> tenantIds = Collections.emptySet();
        if (isClient) {
            //for client
            Client client =
                ApplicationServiceRegistry.getClientApplicationService().internalQuery(clientId);
            RoleId roleId = client.getRoleId();
            Role byId =
                ApplicationServiceRegistry.getRoleApplicationService().internalQueryById(roleId);
            projectId = client.getProjectId();
            permissionIds = byId.getTotalPermissionIds();
            return createJwtToken(
                projectId,
                clientDetail.getAccessTokenValiditySeconds(),
                clientDetail.getRefreshTokenValiditySeconds(),
                clientDetail.getResourceIds(),
                scope,
                clientId,
                null,
                hasRefresh,
                permissionIds,
                Collections.emptySet()
            );
        } else {
            String username = userDetails.getUsername();
            UserId userId = new UserId(username);
            if (scope != null && scope.size() > 0
                &&
                !NOT_USED.equals(scope.stream().findFirst().get())) {
                //only one projectId allowed
                //get tenant project permission
                Optional<String> first = scope.stream().findFirst();
                projectId = new ProjectId(first.get());

                Optional<UserRelation> userRelation =
                    ApplicationServiceRegistry.getUserRelationApplicationService()
                        .query(userId, projectId);
                if (userRelation.isEmpty()) {
                    //auto assign default user role for target project
                    UserRelation newRelation =
                        ApplicationServiceRegistry.getUserRelationApplicationService()
                            .internalOnboardUserToTenant(userId, projectId);
                    log.debug("new tenant user relation for token is {}", newRelation);
                    permissionIds =
                        DomainRegistry.getComputePermissionService().compute(newRelation);
                    projectId = newRelation.getProjectId();
                } else {
                    log.debug("tenant user relation for token is {}", userRelation.get());
                    permissionIds =
                        DomainRegistry.getComputePermissionService().compute(userRelation.get());
                    projectId = userRelation.get().getProjectId();
                }
            } else {
                //get auth project permission and user tenant projects
                Optional<UserRelation> userRelation =
                    ApplicationServiceRegistry.getUserRelationApplicationService()
                        .query(userId, new ProjectId(AppConstant.MT_AUTH_PROJECT_ID));
                userRelation.ifPresent(
                    relation -> log.debug("auth user relation for token is {}", relation));
                if (userRelation.isPresent()) {
                    UserRelation userRelation1 = userRelation.get();
                    permissionIds =
                        DomainRegistry.getComputePermissionService().compute(userRelation1);
                    projectId = userRelation1.getProjectId();
                    if (userRelation1.getTenantIds() != null) {
                        tenantIds = userRelation1.getTenantIds();
                    }
                }
            }
            return createJwtToken(
                projectId,
                clientDetail.getAccessTokenValiditySeconds(),
                clientDetail.getRefreshTokenValiditySeconds(),
                clientDetail.getResourceIds(),
                scope,
                clientId,
                userId,
                hasRefresh,
                permissionIds,
                tenantIds
            );
        }
    }

    private JwtToken grantAuthorizationCode(ClientDetails tokenRequest) {
        return null;
    }

    private JwtToken grantRefreshToken(ClientDetails clientDetails, String refreshToken) {
        //TODO check expire?
        ClientSpringOAuth2Representation clientDetail =
            (ClientSpringOAuth2Representation) clientDetails;
        Set<PermissionId> permissionIds =
            JwtUtility.getPermissionIds(refreshToken).stream().map(PermissionId::new)
                .collect(Collectors.toSet());
        String username = JwtUtility.getUserId(refreshToken);
        UserId userId = new UserId(username);
        String clientId = JwtUtility.getClientId(refreshToken);
        ClientId clientId1 = new ClientId(clientId);
        List<String> scope = JwtUtility.getScopes(refreshToken);
        List<String> collect = JwtUtility.getField("tenantIds", refreshToken);
        Set<ProjectId> tenantIds = collect.stream().map(ProjectId::new).collect(Collectors.toSet());
        String projectId = JwtUtility.getField("projectId", refreshToken);
        ProjectId projectId1 = new ProjectId(projectId);
        return createJwtToken(
            projectId1,
            clientDetail.getAccessTokenValiditySeconds(),
            clientDetail.getRefreshTokenValiditySeconds(),
            clientDetail.getResourceIds(),
            scope,
            clientId1,
            userId,
            true,
            permissionIds,
            tenantIds
        );
    }

    private JwtToken createJwtToken(
        ProjectId projectId,
        int accessTokenSec,
        int refreshTokenSec,
        Collection<String> resourceIds,
        Collection<String> scope,
        ClientId clientId,
        UserId userId,
        boolean hasRefreshToken,
        Set<PermissionId> permissionIds,
        Set<ProjectId> tenantIds
    ) {
        JwtToken jwtToken = new JwtToken();

        Instant now = Instant.now();
        long iatMilli = now.toEpochMilli();
        Date iat = Date.from(now);
        long expMilli = iatMilli + (long) accessTokenSec * 1000;
        Date exp = Date.from(Instant.ofEpochMilli(expMilli));
        //for user & client
        String jwtId = UUID.randomUUID().toString();

        String jwtString = createJwtString(
            userId, resourceIds, iat, exp,
            projectId, jwtId, null,
            scope,
            clientId,
            permissionIds,
            tenantIds
        );
        jwtToken.setSignedAccessToken(jwtString);

        jwtToken.setAccessTokenValidityInSecond(
            (long) accessTokenSec);
        jwtToken.setIssueAtMilli(iatMilli);
        jwtToken.setId(jwtId);
        jwtToken.setPermissionIds(permissionIds);
        jwtToken.setProjectId(projectId);
        jwtToken.setTenantIds(tenantIds);
        jwtToken.setUserId(userId);
        if (hasRefreshToken) {
            //refresh token
            long refreshExpMilli =
                iatMilli + (long) refreshTokenSec * 1000;
            Date refreshExp = Date.from(Instant.ofEpochMilli(refreshExpMilli));
            String refreshJwtId = UUID.randomUUID().toString();

            String refreshJwtString = createJwtString(
                userId, resourceIds, iat, refreshExp,
                projectId, refreshJwtId, jwtId,
                scope,
                clientId,
                permissionIds,
                tenantIds
            );
            jwtToken.setSignedRefreshToken(refreshJwtString);
        }
        return jwtToken;
    }

    private String createJwtString(
        UserId userId,
        Collection<String> aud,
        Date iat,
        Date exp,
        ProjectId projectId,
        String jwtId,
        @Nullable String referredTokenId,
        Collection<String> scope,
        ClientId clientId,
        Collection<PermissionId> permissionIds,
        Collection<ProjectId> tenantIds
    ) {
        KeyPair keyPair = jwtInfoProviderService.getKeyPair();
        JWKSet publicKeys = jwtInfoProviderService.getPublicKeys();
        JWK jwk = publicKeys.getKeys().get(0);
        SignedJWT signedJWT = new SignedJWT(
            new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(jwk.getKeyID())
                .type(JOSEObjectType.JWT)
                .build(),
            new JWTClaimsSet.Builder()
                .claim(USER_ID, userId == null ? null : userId.getDomainId())
                .claim("user_name",
                    userId == null ? null :
                        userId.getDomainId())//required to identify user token or client token
                .audience(new ArrayList<>(aud))
                .issueTime(iat)
                .expirationTime(exp)
                .claim(PROJECT_ID, projectId == null ? null : projectId.getDomainId())
                .claim("ati", referredTokenId)
                .claim("scope",
                    scope.isEmpty() ? List.of("not_used") : scope)//required for refresh
                .jwtID(jwtId)
                .claim(CLIENT_ID, clientId.getDomainId())
                .claim(PERMISSION_IDS,
                    permissionIds.stream().map(DomainId::getDomainId).collect(
                        Collectors.toSet()))
                .claim(TENANT_IDS, tenantIds.stream().map(DomainId::getDomainId).collect(
                    Collectors.toSet()))
                .build());
        try {
            signedJWT.sign(new RSASSASigner(keyPair.getPrivate()));
        } catch (JOSEException e) {
            //TODO add custom error code
            log.error("error during generating token", e);
        }
        return signedJWT.serialize();
    }

}
